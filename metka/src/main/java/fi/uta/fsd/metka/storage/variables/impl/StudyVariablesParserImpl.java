package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.*;
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
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * This class handles default language study variables parsing, creating and merging.
 * Separate class needs to be create for translation file handling since it doesn't do deletion
 * and (hopefully not) creation but instead just adds different translation values to fields that are marked translatable.
 */
// TODO: This class is a mess, clean it up
@Repository
public class StudyVariablesParserImpl implements StudyVariablesParser {

    // Should only be used for custom queries
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

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

    static ParseResult checkRowResultForUpdate(Pair<StatusCode, ? extends ContainerRow> rowPair, ParseResult result) {
        if(rowPair.getLeft() == StatusCode.ROW_CHANGE || rowPair.getLeft() == StatusCode.NEW_ROW || rowPair.getLeft() == StatusCode.ROW_REMOVED) {
            return resultCheck(result, ParseResult.REVISION_CHANGES);
        }
        return result;
    }

    static ParseResult resultCheck(ParseResult result, ParseResult def) {
        return result != ParseResult.REVISION_CHANGES ? def : result;
    }

    @Override
    public ParseResult parse(RevisionData attachment, VariableDataType type, RevisionData study, Language language, DateTimeUserPair info) {
        // Sanity check
        if(type == null) {
            return ParseResult.NO_TYPE_GIVEN;
        }
        if(study == null) {
            return ParseResult.DID_NOT_FIND_STUDY;
        }

        if(info == null) {
            info = DateTimeUserPair.build();
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // **********************
        // StudyAttachment checks
        // **********************

        // Check for file path from attachment
        Pair<StatusCode, ValueDataField> fieldPair = attachment.dataField(ValueDataFieldCall.get(Fields.FILE));
        if(fieldPair.getLeft() != StatusCode.FIELD_FOUND || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            Logger.error(getClass(), "Did not find path in "+attachment.toString()+" even though shouldn't arrive at this point without path.");
            return ParseResult.VARIABLES_FILE_HAD_NO_PATH;
        }

        // Get or create study variables
        fieldPair = study.dataField(ValueDataFieldCall.get("variables"));
        Pair<ReturnResult, RevisionData> dataPair;
        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
            if(language != Language.DEFAULT) {
                // We should not be here, variables should first be created using DEFAULT language file before we get to other languages.
                return ParseResult.NO_DEFAULT_VARIABLES;
            }
            String ext = FilenameUtils.getExtension(attachment.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight().getActualValueFor(Language.DEFAULT).toUpperCase());
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put("study", study.getKey().getId().toString());
            request.getParameters().put("fileid", attachment.getKey().getId().toString());
            request.getParameters().put("varfileid", FilenameUtils.getBaseName(attachment.dataField(ValueDataFieldCall.get(Fields.FILE)).getRight().getActualValueFor(Language.DEFAULT)));
            request.getParameters().put("varfiletype", ext.equals("POR") ? "SPSS Portable" : ext);
            dataPair = create.create(request);
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                Logger.error(getClass(), "Couldn't create new variables revisionable for study "+study.toString()+" and file "+attachment.toString());
                return ParseResult.COULD_NOT_CREATE_VARIABLES;
            }
            fieldPair = study.dataField(
                    ValueDataFieldCall
                            .set("variables", new Value(dataPair.getRight().getKey().getId().toString()), Language.DEFAULT)
                            .setInfo(info));
            result = ParseResult.REVISION_CHANGES;
        } else {
            dataPair = revisions.getLatestRevisionForIdAndType(
                    Long.parseLong(fieldPair.getRight().getActualValueFor(Language.DEFAULT)), false, ConfigurationType.STUDY_VARIABLES);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Couldn't find revision for study variables with id "+fieldPair.getRight().getActualValueFor(Language.DEFAULT)
                        +" even though it's referenced from study "+study.toString());
                return ParseResult.DID_NOT_FIND_VARIABLES;
            }
        }

        RevisionData variablesData = dataPair.getRight();
        if(variablesData.getState() != RevisionState.DRAFT) {
            dataPair = edit.edit(TransferData.buildFromRevisionData(variablesData, RevisionableInfo.FALSE));
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                Logger.error(getClass(), "Couldn't create new DRAFT revision for "+variablesData.getKey().toString());
                return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLES_DRAFT);
            }
            variablesData = dataPair.getRight();
        }

        if(!AuthenticationUtil.isHandler(variablesData)) {
            variablesData.setHandler(AuthenticationUtil.getUserName());
            revisions.updateRevisionData(variablesData);
        }

        // ************************
        // Actual variables parsing
        // ************************
        ParseResult variablesResult = ParseResult.NO_CHANGES;
        VariablesParser parser = null;
        switch(type) {
            case POR:
                // Read POR file
                String studyId = study.dataField(ValueDataFieldCall.get(Fields.STUDYID)).getRight().getActualValueFor(Language.DEFAULT);
                parser = new PORVariablesParser(
                        attachment.dataField(ValueDataFieldCall.get("file")).getRight().getActualValueFor(Language.DEFAULT),
                        language,
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
            long start = System.currentTimeMillis();
            Logger.debug(getClass(), "Starting variables parsing for study");
            variablesResult = parser.parse();
            result = resultCheck(result, variablesResult);
            Logger.debug(getClass(), "Variables parsing for study ended. Spent "+(System.currentTimeMillis()-start)+"ms");
        }
        if(variablesResult == ParseResult.REVISION_CHANGES) {
            ReturnResult updateResult = revisions.updateRevisionData(variablesData);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                Logger.error(getClass(), "Could not update revision data for "+variablesData.toString()+" with result "+updateResult);
                return resultCheck(result, ParseResult.VARIABLES_SERIALIZATION_FAILED);
            }
        }

        return result;
    }



}
