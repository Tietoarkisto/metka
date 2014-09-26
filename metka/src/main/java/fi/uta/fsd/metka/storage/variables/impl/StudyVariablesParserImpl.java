package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static fi.uta.fsd.metka.enums.Language.DEFAULT;

/**
 * This class handles default language study variables parsing, creating and merging.
 * Separate class needs to be create for translation file handling since it doesn't do deletion
 * and (hopefully not) creation but instead just adds different translation values to fields that are marked translatable.
 */
// TODO: This class is a mess, clean it up
@Repository
public class StudyVariablesParserImpl implements StudyVariablesParser {
    private static Logger logger = LoggerFactory.getLogger(StudyVariablesParserImpl.class);

    // Should only be used for custom queries
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRemoveRepository remove;

    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionEditRepository edit;

    static ParseResult checkResultForUpdate(Pair<StatusCode, ? extends DataField> fieldPair, ParseResult result) {
        if(fieldPair.getLeft() == StatusCode.FIELD_UPDATE || fieldPair.getLeft() == StatusCode.FIELD_INSERT) {
            return resultCheck(result, ParseResult.REVISION_CHANGES);
        }
        return result;
    }

    static ParseResult resultCheck(ParseResult result, ParseResult def) {
        return result != ParseResult.REVISION_CHANGES ? def : result;
    }

    @Override
    public ParseResult parse(RevisionData attachment, VariableDataType type, RevisionData study) {
        // Sanity check
        if(type == null) {
            return ParseResult.NO_TYPE_GIVEN;
        }
        if(study == null) {
            return ParseResult.DID_NOT_FIND_STUDY;
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        // **********************
        // StudyAttachment checks
        // **********************
        // Check that study has attached variables file and get the file id,
        // attaching the file should happen before this step so we can expect it to be present
        Pair<StatusCode, ValueDataField> fieldPair = study.dataField(ValueDataFieldCall.get("variablefile"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(DEFAULT)) {
            StatusCode setResult = study.dataField(
                    ValueDataFieldCall.set("variablefile", new Value(attachment.getKey().getId().toString()), DEFAULT).setInfo(info))
                    .getLeft();
            if(!(setResult == StatusCode.FIELD_UPDATE || setResult == StatusCode.FIELD_INSERT)) {
                logger.error("Study update failed with result "+setResult);
                return ParseResult.NO_CHANGES;
            }
        }

        // Check for file path from attachment
        fieldPair = attachment.dataField(ValueDataFieldCall.get("file"));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(DEFAULT)) {
            logger.error("Did not find path in "+attachment.toString()+" even though shouldn't arrive at this point without path.");
            return ParseResult.VARIABLES_FILE_HAD_NO_PATH;
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // Get or create study variables
        fieldPair = study.dataField(ValueDataFieldCall.get("variables"));
        Pair<ReturnResult, RevisionData> dataPair;
        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().hasValueFor(DEFAULT)) {
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put("study", study.getKey().getId().toString());
            request.getParameters().put("fileid", attachment.getKey().getId().toString());
            request.getParameters().put("varfileid", FilenameUtils.getBaseName(attachment.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight().getActualValueFor(DEFAULT)));
            request.getParameters().put("varfiletype", FilenameUtils.getExtension(attachment.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight().getActualValueFor(DEFAULT).toUpperCase()));
            dataPair = create.create(request);
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                logger.error("Couldn't create new variables revisionable for study "+study.toString()+" and file "+attachment.toString());
                return ParseResult.COULD_NOT_CREATE_VARIABLES;
            }
            fieldPair = study.dataField(
                    ValueDataFieldCall
                            .set("variables", new Value(dataPair.getRight().getKey().getId().toString()), DEFAULT)
                            .setInfo(info));
            result = ParseResult.REVISION_CHANGES;
        } else {
            dataPair = revisions.getLatestRevisionForIdAndType(
                    Long.parseLong(fieldPair.getRight().getActualValueFor(DEFAULT)), true, ConfigurationType.STUDY_VARIABLES);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Couldn't find revision for study variables with id "+fieldPair.getRight().getActualValueFor(DEFAULT)
                        +" even though it's referenced from study "+study.toString());
                return ParseResult.DID_NOT_FIND_VARIABLES;
            }
        }

        RevisionData variablesData = dataPair.getRight();
        if(variablesData.getState() != RevisionState.DRAFT) {
            dataPair = edit.edit(TransferData.buildFromRevisionData(variablesData, RevisionableInfo.FALSE));
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                logger.error("Couldn't create new DRAFT revision for "+variablesData.getKey().toString());
                return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLES_DRAFT);
            }
            variablesData = dataPair.getRight();
        }

        // ************************
        // Actual variables parsing
        // ************************
        ParseResult variablesResult = ParseResult.NO_CHANGES;
        VariablesParser parser = null;
        switch(type) {
            case POR:
                // Read POR file
                String studyId = study.dataField(ValueDataFieldCall.get(Fields.STUDYID)).getRight().getActualValueFor(DEFAULT);
                parser = new PORVariablesParser(
                        attachment.dataField(ValueDataFieldCall.get("file")).getRight().getActualValueFor(DEFAULT),
                        variablesData,
                        info,
                        studyId,
                        revisions,
                        remove,
                        create,
                        edit);
                break;
        }
        if(parser != null) {
            variablesResult = parser.parse();
            result = resultCheck(result, variablesResult);
        }
        if(variablesResult == ParseResult.REVISION_CHANGES) {
            ReturnResult updateResult = revisions.updateRevisionData(variablesData);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                logger.error("Could not update revision data for "+variablesData.toString()+" with result "+updateResult);
                return resultCheck(result, ParseResult.VARIABLES_SERIALIZATION_FAILED);
            }
        }

        return result;
    }



}
