package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.storage.variables.enums.ParseResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spssio.por.PORFile;
import spssio.por.input.PORReader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;

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
    private RevisionEditRepository edit;

    private static ParseResult checkResultForUpdate(Pair<StatusCode, ? extends DataField> fieldPair, ParseResult result) {
        if(fieldPair.getLeft() == StatusCode.FIELD_UPDATE || fieldPair.getLeft() == StatusCode.FIELD_INSERT) {
            return resultCheck(result, ParseResult.REVISION_CHANGES);
        }
        return result;
    }

    private static ParseResult resultCheck(ParseResult result, ParseResult def) {
        return result != ParseResult.REVISION_CHANGES ? def : result;
    }

    @Override
    public ParseResult parse(RevisionData attachment, VariableDataType type) {
        long startTime = System.currentTimeMillis();
        // Sanity check
        if(type == null) {
            return ParseResult.NO_TYPE_GIVEN;
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        // Let's get the target study. We can make some assumptions when making this call since we shouldn't be here if this can fail.
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(
                Long.parseLong(attachment.dataField(ValueDataFieldCall.get("study")).getRight().getActualValueFor(DEFAULT)),
                false,
                ConfigurationType.STUDY);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            return ParseResult.DID_NOT_FIND_STUDY;
        }
        RevisionData study = dataPair.getRight();
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
        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().hasValueFor(DEFAULT)) {
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put("study", study.getKey().getId().toString());
            request.getParameters().put("fileid", attachment.getKey().getId().toString());
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
        switch(type) {
            case POR:
                // Read POR file
                variablesResult = handlePorVariables(
                        attachment.dataField(ValueDataFieldCall.get("file")).getRight().getActualValueFor(DEFAULT),
                        variablesData,
                        info);
                result = resultCheck(result, variablesResult);
                break;
        }

        if(variablesResult == ParseResult.REVISION_CHANGES) {
            ReturnResult updateResult = revisions.updateRevisionData(variablesData);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                logger.error("Could not update revision data for "+variablesData.toString()+" with result "+updateResult);
                return resultCheck(result, ParseResult.VARIABLES_SERIALIZATION_FAILED);
            }
        }

        long endTime = System.currentTimeMillis();
        System.err.println("Variable parsing took "+(endTime-startTime)+"ms");
        return result;
    }

    /*@Override
    public ParseResult merge(RevisionData study, VariableDataType type, Configuration studyConfig) {
        long startTime = System.currentTimeMillis();
        // Sanity check
        if(type == null) {
            return ParseResult.NO_TYPE_GIVEN;
        }

        DateTimeUserPair info = DateTimeUserPair.build();

        // **********************
        // StudyAttachment checks
        // **********************
        // Check that study has attached variables file and get the file id,
        // attaching the file should happen before this step so we can expect it to be present
        ValueDataField field = study.dataField(ValueDataFieldCall.get("variablefile").setConfiguration(studyConfig)).getRight();
        Long varFileId;
        if(field == null || !field.containsValueFor(DEFAULT)) {
            return ParseResult.NO_VARIABLES_FILE;
        } else {
            varFileId = field.getValueFor(DEFAULT).valueAsInteger();
        }

        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(varFileId, false, ConfigurationType.STUDY_ATTACHMENT);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Couldn't find revision data, possibly do something
            return ParseResult.DID_NOT_FIND_VARIABLES_FILE;
        }
        RevisionData attachmentData = dataPair.getRight();
        // Check for file path from attachment
        field = attachmentData.dataField(ValueDataFieldCall.get("file")).getRight();
        if(field == null || !field.containsValueFor(DEFAULT)) {
            // TODO: Log exception, something is wrong since no path is attached to the file but we are still trying to parse it for variables
            return ParseResult.VARIABLES_FILE_HAD_NO_PATH;
        }

        ParseResult result = ParseResult.NO_CHANGES;

        // Get or create study variables
        Pair<StatusCode, ValueDataField> fieldPair = study.dataField(ValueDataFieldCall.get("variables").setConfiguration(studyConfig));
        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().containsValueFor(DEFAULT)) {
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put("studyid", study.getKey().getId().toString());
            request.getParameters().put("fileid", varFileId.toString());
            dataPair = create.create(request);
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                logger.error("Couldn't create new variables revisionable for study "+study.toString()+" and file "+attachmentData.toString());
                return ParseResult.COULD_NOT_CREATE_VARIABLES;
            }
            fieldPair = study.dataField(
                    ValueDataFieldCall
                            .set("variables", new Value(dataPair.getRight().getKey().getId().toString()), DEFAULT)
                            .setInfo(info).setConfiguration(studyConfig));
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


        ParseResult variablesResult = ParseResult.NO_CHANGES;
        switch(type) {
            case POR:
                // Read POR file
                variablesResult = handlePorVariables(
                        attachmentData.dataField(ValueDataFieldCall.get("file")).getRight().getActualValueFor(DEFAULT),
                        study.dataField(ValueDataFieldCall.get("studyid")).getRight().getActualValueFor(DEFAULT),
                        variablesData, info);
                result = resultCheck(result, variablesResult);
                break;
        }

        if(variablesResult == ParseResult.REVISION_CHANGES) {
            ReturnResult updateResult = revisions.updateRevisionData(variablesData);
            if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                logger.error("Could not update revision data for "+variablesData.toString()+" with result "+updateResult);
                return resultCheck(result, ParseResult.VARIABLES_SERIALIZATION_FAILED);
            }
        }

        long endTime = System.currentTimeMillis();
        System.err.println("Variable parsing took "+(endTime-startTime)+"ms");
        return result;
    }*/

    /**
     * Parses a por file into a variable structure for a study.
     * At this point the assumption is that file exists, variables revision exists and we can start
     * merging the file to that variables revision.
     * @param path Path to the por file
     * @param variablesData RevisionData of the study variables object used as a base for these variables.
     */
    // TODO: Change this to use create and edit repositories
    private ParseResult handlePorVariables(String path, RevisionData variablesData, DateTimeUserPair info) {
        PORReader reader = new PORReader();
        PORFile por;
        try {
            por = reader.parse(path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading POR-file with path "+path);
            return ParseResult.NO_CHANGES;
        }

        // Group variables to list
        List<PORUtil.PORVariableHolder> variables = new ArrayList<>();
        PORUtil.groupVariables(variables, por.variables, por.labels);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        ParseResult result = ParseResult.NO_CHANGES;

        // Set software field
        Pair<StatusCode, ValueDataField> fieldPair = variablesData.dataField(
                ValueDataFieldCall.set("software", new Value(por.getSoftware()), DEFAULT).setInfo(info));
        result = checkResultForUpdate(fieldPair, result);


        // Set varquantity field
        fieldPair = variablesData.dataField(ValueDataFieldCall.set("varquantity", new Value(por.data.sizeX()+""), DEFAULT).setInfo(info));
        result = checkResultForUpdate(fieldPair, result);

        // Set casequantity field
        fieldPair = variablesData.dataField(ValueDataFieldCall.set("casequantity", new Value(por.data.sizeY()+""), DEFAULT).setInfo(info));
        result = checkResultForUpdate(fieldPair, result);

        // Make VariablesHandler
        VariableHandler handler = new VariableHandler(info, variablesData.dataField(ValueDataFieldCall.get("study")).getRight().getActualValueFor(DEFAULT));

        List<StudyVariableEntity> variableEntities =
                em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariablesId=:studyVariablesId", StudyVariableEntity.class)
                        .setParameter("studyVariablesId", variablesData.getKey().getId())
                        .getResultList();

        List<Pair<StudyVariableEntity, PORUtil.PORVariableHolder>> listOfEntitiesAndHolders = new ArrayList<>();
        for(PORUtil.PORVariableHolder variable : variables) {
            StudyVariableEntity variableEntity = null;
            for(Iterator<StudyVariableEntity> i = variableEntities.iterator(); i.hasNext(); ) {
                variableEntity = i.next();
                if(variableEntity.getVarId().equals(handler.getVarId(variable))) {
                    i.remove();
                    break;
                }
                variableEntity = null;
            }
            listOfEntitiesAndHolders.add(new ImmutablePair<>(variableEntity, variable));
        }

        ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get("variables")).getRight();

        // TODO: Create and utilize RevisionRemoveRepository
        for(StudyVariableEntity variableEntity : variableEntities) {
            // All remaining rows in variableEntities should be removed since no variable was found for them in the current POR-file

            // We don't need to check here if there's draft or not, let's just call draft and logical remove both
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(variableEntity.getId(), false, ConfigurationType.STUDY_VARIABLE);
            // TODO: This operation is somewhat heavy but enough for now
            if(dataPair.getLeft() == ReturnResult.REVISION_FOUND) {
                if(remove.remove(TransferData.buildFromRevisionData(dataPair.getRight(), RevisionableInfo.FALSE)) == RemoveResult.SUCCESS_DRAFT) {
                    dataPair = revisions.getLatestRevisionForIdAndType(variableEntity.getId(), false, ConfigurationType.STUDY_VARIABLE);
                    remove.remove(TransferData.buildFromRevisionData(dataPair.getRight(), RevisionableInfo.FALSE));
                }
            }

            if(variablesContainer != null) {
                // See that respective rows are removed from STUDY_VARIABLES
                //    Remove from variables list
                ReferenceRow reference = variablesContainer.getReferenceWithValue(variableEntity.getId().toString()).getRight();
                if(reference != null) {
                    StatusCode status = variablesContainer.removeReference(reference.getRowId(), variablesData.getChanges(), info).getLeft();
                    if(status == StatusCode.ROW_CHANGE || status == StatusCode.ROW_REMOVED) {
                        result = resultCheck(result, ParseResult.REVISION_CHANGES);
                    }
                }
            }
            //    Remove from variable group list
            // TODO: Handle variable groupings

        }

        // listOfEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable
        if(listOfEntitiesAndHolders.size() > 0 && variablesContainer == null) {
            Pair<StatusCode, ReferenceContainerDataField> containerPair = variablesData.dataField(ReferenceContainerDataFieldCall.set("variables"));
            result = checkResultForUpdate(fieldPair, result);
            variablesContainer = containerPair.getRight();
        }

        if(variablesContainer == null) {
            logger.error("Missing variables container even though it should be present or created");
            return resultCheck(result, ParseResult.NO_VARIABLES_CONTAINER);
        }

        Pair<ReturnResult, Configuration> variableConfiguration = configurations.findLatestConfiguration(ConfigurationType.STUDY_VARIABLE);
        Pair<StatusCode, ValueDataField> studyField = variablesData.dataField(ValueDataFieldCall.get("study"));
        for(Pair<StudyVariableEntity, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            // Iterate through entity/holder pairs. There should always be a holder but missing entity indicates that this is a new variable.
            // After all variables are handled there should be one non removed revisionable per variable in the current por-file.
            // Each revisionable should have an open draft revision (this is a shortcut but it would require doing actual change checking for all variable content to guarantee that no
            // unnecessary revisions are created. This is not required and so a new draft is provided per revisionable).
            // Variables entity should have an open draft revision that includes references to all variables as well as non grouped references for all variables that previously were
            // not in any groups.

            StudyVariableEntity variableEntity = pair.getLeft();
            PORUtil.PORVariableHolder variable = pair.getRight();
            String varId = handler.getVarId(variable);
            Pair<ReturnResult, RevisionData> dataPair;
            if(variableEntity == null) {
                RevisionCreateRequest request = new RevisionCreateRequest();
                request.setType(ConfigurationType.STUDY_VARIABLE);
                request.getParameters().put("study", studyField.getRight().getActualValueFor(DEFAULT));
                request.getParameters().put("variablesid", variablesData.getKey().getId().toString());
                request.getParameters().put("varid", varId);
                dataPair = create.create(request);
                if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                    logger.error("Couldn't create new variable revisionable for studyid "+studyField.getRight().getActualValueFor(DEFAULT)+" and variables "+variablesData.toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLES);
                }
            } else {
                dataPair = revisions.getLatestRevisionForIdAndType(variableEntity.getId(), false, ConfigurationType.STUDY_VARIABLE);
                if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    logger.error("Couldn't find revision for study variable with id "+variableEntity.getId()
                            +" even though StudyVariableEntity existed.");
                    return resultCheck(result, ParseResult.DID_NOT_FIND_VARIABLE);
                }
            }

            // Add Saved reference if missing
            // TODO: Add saved reference to ungrouped goupings if not present in any group
            variablesContainer.getOrCreateReferenceWithValue(dataPair.getRight().getKey().getId().toString(), variablesData.getChanges(), info).getRight();
            result = resultCheck(result, ParseResult.REVISION_CHANGES);

            RevisionData variableData = dataPair.getRight();
            if(variableData.getState() != RevisionState.DRAFT) {
                dataPair = edit.edit(TransferData.buildFromRevisionData(variablesData, RevisionableInfo.FALSE));
                if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                    logger.error("Couldn't create new DRAFT revision for "+variableData.getKey().toString());
                    return resultCheck(result, ParseResult.COULD_NOT_CREATE_VARIABLE_DRAFT);
                }
                variablesData = dataPair.getRight();
            }

            // Merge variable to variable revision
            ParseResult mergeResult = handler.mergeToData(variableData, variable);

            if(mergeResult == ParseResult.REVISION_CHANGES) {
                ReturnResult updateResult = revisions.updateRevisionData(variableData);
                if(updateResult != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                    logger.error("Could not update revision data for "+variableData.toString()+" with result "+updateResult);
                }
            }
        }

        // TODO: After all these steps initiate a re-index on all affected revisions (which can include multiple revisions of one revisionable in the case of logical removal).
        return result;
    }

    /**
     * Builds variables.
     * Doesn't require transaction as such but should be used within one and with EntityManager
     * so that possible changes can be persisted after they are made.
     * Should only work with RevisionData for StudyVariableEntity revisions.
     *
     * Since there is no configuration for variable parsing we don't require STUDY_VARIABLE configuration
     * here. If something changes in the configuration then manual changes need to be made here in any case.
     */
    private static class VariableHandler {
        private DateTimeUserPair info;
        private String studyId;

        VariableHandler(DateTimeUserPair info, String studyId) {
            this.info = info;
            this.studyId = studyId;
        }

        String getVarId(PORUtil.PORVariableHolder variable) {
            if(variable == null) {
                return null;
            }
            return studyId+"_"+variable.asVariable().getName();
        }

        /**
         * Handles one PORVariableHolder inserting all relevant information into given variable revision.
         * Handles only fields that are not user editable and so doesn't need to care about not overwriting
         * existing data.
         *
         * @param variableRevision Variable revision where data is inserted
         * @param variable PORVariableHolder containing singe variable data to be parsed
         */
        ParseResult mergeToData(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Sanity check
            if(variableRevision == null || variable == null) {
                return ParseResult.NO_CHANGES;
            }

            ParseResult result = ParseResult.NO_CHANGES;

            // Set varname field
            Pair<StatusCode, ValueDataField> fieldPair = variableRevision.dataField(
                    ValueDataFieldCall.set("varname", new Value(variable.asVariable().getName()), DEFAULT).setInfo(info));
            checkResultForUpdate(fieldPair, result);

            // Set varlabel field
            String label = !StringUtils.hasText(variable.asVariable().label)
                    ? "[Muuttujalta puuttuu LABEL tieto]"
                    : variable.asVariable().label;
            fieldPair = variableRevision.dataField(ValueDataFieldCall.set("varlabel", new Value(label), DEFAULT).setInfo(info));
            checkResultForUpdate(fieldPair, result);

            // TODO: Copy varlabel content to a row in qustnlits if there's not already rows in there
            /*Pair<StatusCode, ContainerDataField> qstns = variableRevision.dataField(ContainerDataFieldCall.set("qstnlits"));
            checkResultForUpdate(qstns, result);

            if(!qstns.getRight().hasRowsFor(DEFAULT)) {
                Pair<StatusCode, DataRow> row = qstns.getRight().insertNewDataRow(DEFAULT, variable);
            }*/

            // Set valuelabels CONTAINER
            ParseResult operationResult = setValueLabels(variableRevision, variable);
            result = resultCheck(result, operationResult);
            // Set categories CONTAINER
            operationResult = setCategories(variableRevision, variable);
            result = resultCheck(result, operationResult);
            // Set interval
            operationResult = setInterval(variableRevision, variable);
            result = resultCheck(result, operationResult);
            // Set statistics CONTAINER
            operationResult = setStatistics(variableRevision, variable);
            result = resultCheck(result, operationResult);

            return result;
        }

        /**
         * Merge value labels data to given variable revision.
         * Creates missing fields as needed, uses existing ones if present.
         *
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private ParseResult setValueLabels(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            ContainerDataField valueLabels = variableRevision.dataField(ContainerDataFieldCall.get("valuelabels")).getRight();

            if(variable.getLabelsSize() == 0 && valueLabels == null) {
                // No labels and no old container. Nothing needs to be done
                return ParseResult.NO_CHANGES;
            }

            ParseResult result = ParseResult.NO_CHANGES;

            // Check to see if we need to initialise valueLabels container
            // There has to be labels to warrant a valueLabels container creation if it's not present already
            if(variable.getLabelsSize() > 0 && valueLabels == null) {
                valueLabels = variableRevision.dataField(ContainerDataFieldCall.set("valuelabels")).getRight();
                result = ParseResult.REVISION_CHANGES;
            }

            // Gather existing value labels to a separate list to allow for obsolete row checking and preserving por-file defined order
            List<DataRow> rows = gatherAndClear(valueLabels);

            // Add container rows
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                DataRow row = popOrCreateAndInsertRow(valueLabels, rows, "value", label.getValue());
                result = resultCheck(result, setValueLabelRow(row, label, variableRevision.getChanges()));
            }

            result = resultCheck(result, removeObsoleteRows(rows, valueLabels, variableRevision.getChanges(), info));

            return result;
        }

        private ParseResult setValueLabelRow(DataRow row, PORUtil.PORVariableValueLabel label, Map<String, Change> changeMap) {
            // TODO: Check return values to detect changes and problems
            ParseResult result = ParseResult.NO_CHANGES;

            // We know that this row is needed and so we can set it to not removed state no matter if it was removed previously or not
            StatusCode restoreResult = row.restore(changeMap, DEFAULT, info);
            if(restoreResult == StatusCode.ROW_CHANGE) {
                result = ParseResult.REVISION_CHANGES;
            }

            Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                    ValueDataFieldCall.set("value", new Value(label.getValue()), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            fieldPair = row.dataField(ValueDataFieldCall.set("label", new Value(label.getLabel()), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            fieldPair = row.dataField(ValueDataFieldCall.set("missing", new Value(label.isMissing() ? "Y" : null), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            return result;
        }

        /**
         * Merge categories container to given variable revision.
         * Creates all missing fields that are needed but uses existing ones if present.
         * Checks the need of frequency statistics and calculates them as needed based on multiple criteria.
         * Frequency statistics and value labels are not directly linked but instead if a value happens to match a value label
         * then the label is printed as part of the statistics.
         *
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private ParseResult setCategories(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            boolean getValidFreq = false;
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                if(!label.isMissing()) {
                    getValidFreq = true;
                    break;
                }
            }

            // Calculate frequencies
            Map<PORUtil.PORVariableData, Integer> valid = new HashMap<>();
            Map<PORUtil.PORVariableData, Integer> missing = new HashMap<>();
            Integer sysmiss = 0;

            for(PORUtil.PORVariableData value : variable.getData()) {
                switch(value.getType()) {
                    case SYSMISS:
                        sysmiss++;
                        break;
                    case STRING:
                        // For string checking the variable must be string typed and have labels. Strings without labels won't be frequency checked
                        if(variable.isNumeric() || variable.getLabelsSize() == 0) {
                            break;
                        }

                        PORUtil.PORVariableDataString s = (PORUtil.PORVariableDataString)value;
                        // Check frequency only for string value that matches a label
                        if(variable.getLabel(s.getValue()) == null) {
                            break;
                        }

                        // If value is an user missing value add it to missing values frequency map, otherwise add it to valid frequencies map if valid frequencies are needed
                        if(variable.isUserMissing(s.getValue())) {
                            increaseFrequencyValue(value, missing);
                        } else if(getValidFreq) {
                            increaseFrequencyValue(value, valid);
                        }
                        break;
                    case NUMERIC:
                        // For numeric frequency checking the variable must be numeric and contain either labels or/and missing values
                        if(!variable.isNumeric() || (variable.getLabelsSize() == 0 && variable.getMissingSize() == 0)) {
                            break;
                        }
                        PORUtil.PORVariableDataNumeric n = (PORUtil.PORVariableDataNumeric)value;
                        // If value is an user missing value add it to missing values frequency map, otherwise add it to valid frequencies map if valid frequencies are needed
                        if(variable.isUserMissing(n.getValue())) {
                            increaseFrequencyValue(value, missing);
                        } else if(getValidFreq) {
                            increaseFrequencyValue(value, valid);
                        }
                        break;
                }
            }

            ParseResult result = ParseResult.NO_CHANGES;

            // Get categories container
            ContainerDataField categories = variableRevision.dataField(ContainerDataFieldCall.get("categories")).getRight();
            // Get changeMap reference since it's used multiple times
            Map<String, Change> changeMap = variableRevision.getChanges();

            if(valid.size() == 0 && missing.size() == 0) {
                // No frequencies, remove all frequency rows and return
                if(categories != null) {
                    for(Integer id : categories.getRowIdsFor(DEFAULT)) {
                        StatusCode removeResult = categories.removeRow(id, changeMap, info).getLeft();
                        if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                            result = ParseResult.REVISION_CHANGES;
                        }
                    }
                }
                return result;
            }

            // At least some frequencies, check that we have a container
            if(categories == null) {
                categories = variableRevision.dataField(ContainerDataFieldCall.set("categories")).getRight();
                result = ParseResult.REVISION_CHANGES;
            }

            // Gather all old rows
            List<DataRow> rows = gatherAndClear(categories);

            // Add valid frequencies
            result = resultCheck(result, setFrequencies(variableRevision, variable, categories, valid, changeMap, rows, false));

            // Add missing frequencies
            result = resultCheck(result, setFrequencies(variableRevision, variable, categories, missing, changeMap, rows, true));

            // Add SYSMISS if they exist and there are other frequencies, otherwise remove possible existing SYSMISS related inserts
            DataRow sysmissRow = popRowWithFieldValue(rows, "value", "SYSMISS");
            if((valid.size() > 0 || missing.size() > 0) && sysmiss > 0) {
                if(sysmissRow == null) {
                    sysmissRow = DataRow.build(categories);
                }
                categories.addRow(DEFAULT, sysmissRow);

                result = resultCheck(result, setCategoryRow(sysmissRow, "SYSMISS", "SYSMISS", sysmiss, true, changeMap));
            } else if(sysmissRow != null) {
                // SYSMISS row is not needed but existed previously, mark as removed
                categories.addRow(DEFAULT, sysmissRow); // Insert it back to to categories container before removal or the change doesn't make any sense
                StatusCode removeResult = categories.removeRow(sysmissRow.getRowId(), changeMap, info).getLeft();
                if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                    result = resultCheck(result, ParseResult.REVISION_CHANGES);
                }
            }

            return result;
        }

        /**
         * Helper function for category checking.
         * Increases an integer value in a map based on a given key. If key doesn't exist previously then puts it to map with value 1
         * @param key Frequency map key
         * @param map Frequency map
         */
        private void increaseFrequencyValue(PORUtil.PORVariableData key, Map<PORUtil.PORVariableData, Integer> map) {
            if(map.containsKey(key)) {
                map.put(key, map.get(key)+1);
            } else {
                map.put(key, 1);
            }
        }

        /**
         * Helper method for grouping and processing value frequencies.
         *
         * @param revision RevisionData of variableRevision, needed for row index
         * @param variable Current variable, needed for value labels
         * @param target Container where rows are inserted
         * @param frequencies New frequency values
         * @param changeMap Change map that changes to target are under.
         * @param rows List of DataRows containing old frequencies
         * @param missing Are values in rows to be considered missing values
         */
        private ParseResult setFrequencies(RevisionData revision, PORUtil.PORVariableHolder variable, ContainerDataField target,
                                    Map<PORUtil.PORVariableData, Integer> frequencies, Map<String, Change> changeMap,
                                    List<DataRow> rows, boolean missing) {
            // Add frequencies to target, frequencies map will be empty if frequencies of this type are not required so this step can be passed in that case.
            if(frequencies.size() == 0) {
                return ParseResult.NO_CHANGES;
            }

            ParseResult result = ParseResult.NO_CHANGES;

            List<PORUtil.PORVariableData> sortedKeys = new ArrayList<>(frequencies.keySet());
            Collections.sort(sortedKeys, new PORUtil.PORVariableDataComparator());
            for(PORUtil.PORVariableData value : sortedKeys) {
                DataRow row = popOrCreateAndInsertRow(target, rows, "value", value.toString());
                PORUtil.PORVariableValueLabel label = variable.getLabel(value.toString());
                Integer freq = frequencies.get(value);

                result = resultCheck(result, setCategoryRow(row, value.toString(), (label != null) ? label.getLabel() : null, freq, missing, changeMap));
            }
            return result;
        }

        /**
         * Helper function for setting category row values
         * @param row Row where the values are set
         * @param value Category value for the row
         * @param label Possible label for the row, can be null
         * @param stat Frequency statistic for the row
         * @param missing Is the category a missing category or not
         * @param changeMap Where changes are logged
         */
        private ParseResult setCategoryRow(DataRow row, String value, String label, Integer stat, boolean missing, Map<String, Change> changeMap) {
            ParseResult result = ParseResult.NO_CHANGES;

            StatusCode restoreResult = row.restore(changeMap, DEFAULT, info);
            if(restoreResult == StatusCode.ROW_CHANGE) {
                result = ParseResult.REVISION_CHANGES;
            }
            Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                    ValueDataFieldCall.set("value", new Value(value), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            fieldPair = row.dataField(ValueDataFieldCall.set("label", new Value(label), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            fieldPair = row.dataField(ValueDataFieldCall.set("categorystat", new Value(stat.toString()), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            fieldPair = row.dataField(ValueDataFieldCall.set("missing", new Value(missing ? "Y" : null), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            return result;
        }

        /**
         * Set varinterval attribute for given variable revision.
         *
         * @param variableRevision Variable revision requiring varinterval information
         * @param variable Variable used to determine varinterval value
         */
        private ParseResult setInterval(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Start from the assumption that the variable is continuous and work from there
            boolean continuous = true;

            if(!variable.isNumeric()) {
                continuous = false;
            }

            if(continuous && variable.getLabelsSize() > 0) {
                for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                    if(!label.isMissing()) {
                        continuous = false;
                        break;
                    }
                }
            }

            return checkResultForUpdate(
                    variableRevision.dataField(ValueDataFieldCall
                            .set("varinterval", new Value(continuous ? "contin" : "discrete"), DEFAULT)
                            .setInfo(info)),
                    ParseResult.NO_CHANGES);
        }

        /**
         * Sets statistics for single variable (min, max etc.)
         * Statistics are calculated only for numerical variables and all statistics are calculated for all numerical variables.
         * SYSMISS and user missing values are not valid values for statistics
         *
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private ParseResult setStatistics(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Get statistics container
            ContainerDataField statistics = variableRevision.dataField(ContainerDataFieldCall.get("statistics")).getRight();
            // Get changeMap
            Map<String, Change> changeMap = variableRevision.getChanges();

            ParseResult result = ParseResult.NO_CHANGES;

            if(!variable.isNumeric()) {
                // Variable is not numeric and should not contain statistics.
                if(statistics != null) {
                    for(Integer id : statistics.getRowIdsFor(DEFAULT)) {
                        StatusCode removeResult = statistics.removeRow(id, changeMap, info).getLeft();
                        if(removeResult == StatusCode.ROW_CHANGE || removeResult == StatusCode.ROW_REMOVED) {
                            result = ParseResult.REVISION_CHANGES;
                        }
                    }
                }
                return result;
            }

            // Get valid numerical data as base for calculating statistics
            List<PORUtil.PORVariableDataNumeric> data = variable.getValidNumericalData();
            // amount of valid values is used multiple times so it makes sense to lift it as a separate value
            Integer values = data.size();

            // This variable should have statistics, create if missing
            if(statistics == null) {
                statistics = variableRevision.dataField(ContainerDataFieldCall.set("statistics")).getRight();
                result = ParseResult.REVISION_CHANGES;
            }

            List<DataRow> rows = gatherAndClear(statistics);

            // These variables are needed multiple times so define them separately here
            DataRow row;
            String type;
            String statisticstype = "statisticstype";
            String statisticvalue = "statisticvalue";

            // Set vald
            type = "vald"; // Valid values statistic
            row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
            Pair<StatusCode, ValueDataField> fieldPair = row.dataField(
                    ValueDataFieldCall.set(statisticvalue, new Value(values.toString()), DEFAULT).setInfo(info).setChangeMap(changeMap));
            checkResultForUpdate(fieldPair, result);

            // Set min
            type = "min";
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                String min = Collections.min(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                fieldPair = row.dataField(ValueDataFieldCall.set(statisticvalue, new Value(min), DEFAULT).setInfo(info).setChangeMap(changeMap));
                checkResultForUpdate(fieldPair, result);
            }

            // Set max
            type = "max";
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                String max = Collections.max(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                fieldPair = row.dataField(ValueDataFieldCall.set(statisticvalue, new Value(max), DEFAULT).setInfo(info).setChangeMap(changeMap));
                checkResultForUpdate(fieldPair, result);
            }

            // Set mean
            type = "mean";
            Double mean = 0D;
            // If there are no values or variable is continuous don't add mean
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                Integer denom = 0;
                for(PORUtil.PORVariableDataNumeric varD : data) {
                    mean += varD.getValue();
                    denom++;
                }
                mean = mean / denom;
                fieldPair = row.dataField(ValueDataFieldCall.set(statisticvalue, new Value(mean.toString()), DEFAULT).setInfo(info).setChangeMap(changeMap));
                checkResultForUpdate(fieldPair, result);
            }

            // Set stdev
            type = "stdev";
            // If there are no values or variable is continuous don't add deviation
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                Double deviation = 0D;
                Integer denom = 0;
                for(PORUtil.PORVariableDataNumeric varD : data) {
                    Double value = varD.getValue();
                    if(value != null) {
                        deviation += Math.pow((value - mean), 2);
                        denom++;
                    }
                }
                deviation = Math.sqrt(deviation/denom);
                fieldPair = row.dataField(ValueDataFieldCall.set(statisticvalue, new Value(deviation.toString()), DEFAULT).setInfo(info).setChangeMap(changeMap));
                checkResultForUpdate(fieldPair, result);
            }

            result = resultCheck(result, removeObsoleteRows(rows, statistics, changeMap, info));

            return result;
        }

        /**
         * Helper method for handling and organising container rows.
         * Takes a collection of rows, finds a row based on a field and removes it from the given
         * collection.
         * Assumption is that the collection is not the actual rows list of a ContainerDataField
         * but some other collection used for organising rows during operations.
         *
         * @param rows Collection of rows to search through
         * @param key Field key of the field where the value should be found
         * @param value Value to be searched for, should be non empty string
         * @return First DataRow to match the given value, null if no row was found
         */
        private DataRow popRowWithFieldValue(Collection<DataRow> rows, String key, String value) {
            for(Iterator<DataRow> i = rows.iterator(); i.hasNext(); ) {
                DataRow row = i.next();
                Pair<StatusCode, ValueDataField> field = row.dataField(ValueDataFieldCall.get(key));
                if(field.getLeft() == StatusCode.FIELD_FOUND && field.getRight().valueForEquals(DEFAULT, value)) {
                    i.remove();
                    return row;
                }
            }
            return null;
        }

        /**
         * Helper method for handling and organising container rows.
         * Searches given collection for a row with given value in given field.
         * If row was not found then creates a new row and inserts it into provided container.
         * No change handling is necessary since some set operation should follow always after
         * calling this method.
         *
         * @param target Target container where the row will be set
         * @param rows Collection of rows to search through for correct existing row
         * @param key Field key of the field where given value should be
         * @param value Value to search for
         * @return Either an existing or newly created DataRow that has been inserted to the given container already
         */
        private DataRow popOrCreateAndInsertRow(ContainerDataField target, Collection<DataRow> rows, String key, String value) {
            DataRow row = popRowWithFieldValue(rows, key, value);
            if(row == null) {
                row = DataRow.build(target);
            }
            target.addRow(DEFAULT, row);
            return row;
        }

        /**
         * Removes obsolete rows by placing them in given container and then running them through remove method
         * @param rows Collection of rows that are obsolete
         * @param target Container where removed rows are added
         * @param changeMap Change map where target containers changes should be
         */
        private static ParseResult removeObsoleteRows(Collection<DataRow> rows, ContainerDataField target, Map<String, Change> changeMap, DateTimeUserPair info) {
            ParseResult result = ParseResult.NO_CHANGES;
            for(DataRow row : rows) {
                target.addRow(DEFAULT, row);
                StatusCode status = target.removeRow(row.getRowId(), changeMap, info).getLeft();
                if(status == StatusCode.ROW_CHANGE || status == StatusCode.ROW_REMOVED) {
                    result = ParseResult.REVISION_CHANGES;
                }
            }
            return result;
        }

        private static List<DataRow> gatherAndClear(ContainerDataField field) {
            if(field.getRowsFor(DEFAULT) == null) {
                return new ArrayList<>();
            }
            List<DataRow> rows = new ArrayList<>(field.getRowsFor(DEFAULT));
            field.getRowsFor(DEFAULT).clear();
            return rows;
        }
    }
}
