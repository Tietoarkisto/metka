package fi.uta.fsd.metka.data.variableParsing.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyAttachmentEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.enums.VariableDataType;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.data.variableParsing.StudyVariablesParser;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spssio.por.PORFile;
import spssio.por.input.PORReader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;
import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

@Repository
public class StudyVariablesParserImpl implements StudyVariablesParser {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private VariablesFactory factory;

    @Override
    public boolean merge(RevisionData study, VariableDataType type, Configuration studyConfig) throws IOException {
        boolean result = false;
        // Sanity check
        if(type == null) {
            return result;
        }

        LocalDateTime time = new LocalDateTime();

        // *********************
        // StudyAttachment checks
        // *********************
        // Check that study has attached variables file and get the file id, attaching the file should happen before this step so we can expect it to be present
        SavedDataField field = getSavedDataFieldFromRevisionData(study, "variablefile", studyConfig);
        Integer varFileId;
        if(field == null || !field.hasValue()) {
            // TODO: Log exception, there is no attached variables file, can't continue.
            return result;
        } else {
            varFileId = stringToInteger(field.getActualValue());
        }

        // Get variable file revision data
        StudyAttachmentEntity attachmentEntity = em.find(StudyAttachmentEntity.class, varFileId);
        RevisionEntity attachmentRevision = null;
        RevisionData attachmentData = null;
        if(attachmentEntity == null || attachmentEntity.getLatestRevisionNo() == null) {
            // TODO: Log exception since something is very wrong. There should definitely be an attachment if we get to this point
            return result;
        } else {
            attachmentRevision = em.find(RevisionEntity.class, new RevisionKey(attachmentEntity.getId(), attachmentEntity.getLatestRevisionNo()));
        }

        // Make a sanity check just in case
        if(attachmentRevision == null || StringUtils.isEmpty(attachmentRevision.getData())) {
            // TODO: Attachment revision or revision data missing, can't continue, log exception
            return result;
        } else {
            attachmentData = json.readRevisionDataFromString(attachmentRevision.getData());
        }

        // Check for file path from attachment
        field = getSavedDataFieldFromRevisionData(attachmentData, "file");
        if(field == null || !field.hasValue() || StringUtils.isEmpty(field.getActualValue())) {
            // TODO: Log exception, something is wrong since no path is attached to the file but we are still trying to parse it for variables
            return result;
        }

        // *********************
        // StudyVariables checks
        // *********************
        StudyVariablesEntity variablesEntity = null;
        field = getSavedDataFieldFromRevisionData(study, "variables", studyConfig);
        if(field == null) {
            field = new SavedDataField("variables");
            study.putField(field);
            result = true;
        }
        // Check so see if a variables object exists for this study, if not then create it otherwise create new
        List<StudyVariablesEntity> varsEntityList =
                em.createQuery(
                        "SELECT v FROM StudyVariablesEntity v " +
                                "WHERE v.studyId=:study", StudyVariablesEntity.class)
                        .setParameter("study", study.getKey().getId())
                        .getResultList();
        if(varsEntityList.size() == 0) {
            // Need to create new variables object since none apparently exist for this study.
            variablesEntity = new StudyVariablesEntity();
            variablesEntity.setStudyId(study.getKey().getId());
            em.persist(variablesEntity);
        } else {
            if(varsEntityList.size() > 1) {
                // TODO: Log error, this situation should not happen, each study should only have one variables entity
                return result;
            } else {
                // Get the variables entity
                variablesEntity = varsEntityList.get(0);
            }
        }

        // Check to see if study knows about the variables, if not attach them, if there's a value then check it matches found variables
        if(!field.hasValue()) {
            field.setModifiedValue(setSimpleValue(createSavedValue(time), variablesEntity.getId().toString()));
            result = true;
        } else {
            if(!field.getActualValue().equals(variablesEntity.getId().toString())) {
                // TODO: Log error, there's a discrepancy between saved value and found variables revisionable
                return result;
            }
        }

        // Get variables revision
        RevisionEntity variablesRevision = null;
        if(variablesEntity.getLatestRevisionNo() == null) {
            // No initial revision, assume created here and add initial revision
            variablesRevision = variablesEntity.createNextRevision();
            variablesRevision.setState(RevisionState.DRAFT);

            /*
             * creates initial dataset for the first draft any exceptions thrown should force rollback
             * automatically.
             * This assumes the entity has empty data field and is a draft.
            */
            factory.newStudyVariables(variablesRevision, study.getKey().getId(), varFileId);
            em.persist(variablesRevision);

            variablesEntity.setLatestRevisionNo(variablesRevision.getKey().getRevisionNo());

        } else if(variablesEntity.getCurApprovedNo() != null &&
                variablesEntity.getCurApprovedNo().equals(variablesEntity.getLatestRevisionNo())) {
            // No draft, should draft be created for merge or can we use approved revision?
            // For now create draft
            RevisionEntity oldVariables = em.find(RevisionEntity.class, new RevisionKey(variablesEntity.getId(), variablesEntity.getLatestRevisionNo()));
            RevisionData oldData = json.readRevisionDataFromString(oldVariables.getData());

            variablesRevision = variablesEntity.createNextRevision();
            variablesRevision.setState(RevisionState.DRAFT);
            RevisionData newData = DataFactory.createNewRevisionData(variablesRevision, oldData);
            variablesRevision.setData(json.serialize(newData));

            em.persist(variablesRevision);
            variablesEntity.setLatestRevisionNo(variablesRevision.getKey().getRevisionNo());
        } else {
            variablesRevision = em.find(RevisionEntity.class, new RevisionKey(variablesEntity.getId(), variablesEntity.getLatestRevisionNo()));
        }

        RevisionData variablesData = json.readRevisionDataFromString(variablesRevision.getData());

        // Unecessary sanity checks
        if(variablesData == null) {
            // Should not be possible
            return result;
        }
        field = getSavedDataFieldFromRevisionData(variablesData, "study");
        if(field == null || !field.hasValue() || !field.getActualValue().equals(study.getKey().getId().toString())) {
            // TODO: Something wrong with study link, log error, can't continue
            return result;
        }
        field = getSavedDataFieldFromRevisionData(variablesData, "file");
        if(field == null || !field.hasValue() || !field.getActualValue().equals(varFileId)) {
            // TODO: Something wrong with variable file link, log error, can't continue
        }

        // ************************
        // Actual variables parsing
        // ************************
        // File path to the actual variables file
        field = getSavedDataFieldFromRevisionData(attachmentData, "file");
        String filePath = field.getActualValue();

        switch(type) {
            case POR:
                result = true;
                // Read POR file
                handlePorVariables(filePath, variablesData);
                break;
        }
        variablesRevision.setData(json.serialize(variablesData));
        // Final merge to make sure that changes go to database if for some reason variablesRevision has stopped being managed.
        em.merge(variablesRevision);
        return result;
    }

    /**
     * Parses a por file into a variable structure for a study.
     * At this point the assumption is that file exists, variables revision exists and we can start
     * merging the file to that variables revision.
     * @param path Path to the por file
     * @param variablesData RevisionData of the study variables object used as a base for these variables.
     * @return
     */
    private void handlePorVariables(String path, RevisionData variablesData) throws IOException {
        PORReader reader = new PORReader();
        PORFile por = reader.parse(path);

        // Group variables to list
        List<PORUtil.PORVariableHolder> variables = new ArrayList<>();
        PORUtil.groupVariables(variables, por.variables, por.labels);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        // Make VariablesHandler
        VariableHandler handler = new VariableHandler();

        // Iterate through variables merging data to variables container
        for(PORUtil.PORVariableHolder variable : variables) {
            handler.initWithVariable(variable);
            String varId = handler.getVariableId();
            // Look for matching variable revisionable.
            List<StudyVariableEntity> variableEntities =
                    em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariablesId=:studyVariablesId AND e.variableId=:variableId", StudyVariableEntity.class)
                    .setParameter("studyVariablesId", variablesData.getKey().getId())
                    .setParameter("variableId", varId)
                    .getResultList();

            StudyVariableEntity variableEntity = null;
            // If one doesn't exist then create one.
            if(variableEntities.size() == 0) {
                // No variable yet, create variable
                variableEntity = new StudyVariableEntity();
                variableEntity.setStudyVariablesId(variablesData.getKey().getId());
                variableEntity.setVariableId(varId);
                em.persist(variableEntity);
            } else if(variableEntities.size() > 1) {
                // TODO: Log error and skip variable
                continue;
            } else {
                variableEntity = variableEntities.get(0);
            }

            // Look for revision on variable revisionable
            RevisionEntity variableRevision = null;
            if(variableEntity.getLatestRevisionNo() == null) {
                // If one doesn't exist create one
                // No initial revision, assume created here and add initial revision
                variableRevision = variableEntity.createNextRevision();
                factory.newVariable(variableRevision, variablesData.getKey().getId());
                em.persist(variableRevision);

                variableEntity.setLatestRevisionNo(variableRevision.getKey().getRevisionNo());
            } else if(variableEntity.getCurApprovedNo() != null && variableEntity.getCurApprovedNo().equals(variableEntity.getLatestRevisionNo())) {
                // No draft, should draft be created for merge or can we use approved revision?
                // For now create draft
                RevisionEntity oldVariables = em.find(RevisionEntity.class, new RevisionKey(variableEntity.getId(), variableEntity.getLatestRevisionNo()));
                RevisionData oldData = json.readRevisionDataFromString(oldVariables.getData());

                variableRevision = variableEntity.createNextRevision();
                RevisionData newData = DataFactory.createNewRevisionData(variableRevision, oldData);
                variableRevision.setData(json.serialize(newData));

                em.persist(variableRevision);
                variableEntity.setLatestRevisionNo(variableRevision.getKey().getRevisionNo());
            } else {
                // If there is a draft then use that
                variableRevision = em.find(RevisionEntity.class, new RevisionKey(variableEntity.getId(), variableEntity.getLatestRevisionNo()));
            }

            RevisionData variableData = json.readRevisionDataFromString(variableRevision.getData());

            // Merge variable to variable revision
            handler.mergeToData(variableData);

            // Persis revision with new revision data
            variableRevision.setData(json.serialize(variableData));
        }
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
        private LocalDateTime time;
        private PORUtil.PORVariableHolder variable;

        VariableHandler() {
            this.time = new LocalDateTime();
        }

        void initWithVariable(PORUtil.PORVariableHolder variable) {
            this.variable = variable;
        }

        String getVariableId() {
            if(variable == null) {
                return null;
            }
            return variable.asVariable().getName();
        }

        void mergeToData(RevisionData variableData) {
            // Set varname field using setRowField method
            setRowField(variableData, "varname", variable.asVariable().getName());
            // Set varid field using setRowField method
            setRowField(variableData, "varid", variable.asVariable().getName());
            // Set label field using setRowField method
            setRowField(variableData, "varlabel", variable.asVariable().label);
            // Set categories CONTAINER
            /*setCategories(variable);
            // Set statistics CONTAINER
            setStatistics(variable);*/
        }

        /**
         * Sets a RevisionData field with given key to value provided.
         * If field does not exist then creates the field.
         *
         * TODO: Provide with ChangeContainer
         *
         * @param variableData RevisionData to be modified
         * @param key Field key
         * @param value Value to be inserted in field
         */
        private void setRowField(RevisionData variableData, String key, String value) {
            SavedDataField field = (SavedDataField)variableData.getField(key);
            if(field == null) {
                field = new SavedDataField(key);
                variableData.putField(field);
            }
            if(!field.hasValue() || !field.valueEquals(value)) {
                field.setModifiedValue(setSimpleValue(createSavedValue(time), value));
                variableData.putChange(new Change(key));
            }
        }
    }

    /*private static class VariablesHandlerOld {


        *//**
         * Merge given variable to the data in this handler.
         * @param variable To be merged
         *//*
        void mergeToData(PORUtil.PORVariableHolder variable) {
            // Start merge process
            start(variable.asVariable().getName());
            // Set varid field using setRowField method
            setRowField(currentRow, "varid", variable.asVariable().getName());
            // Set label field using setRowField method
            setRowField(currentRow, "varlabel", variable.asVariable().label);
            // Set categories CONTAINER
            setCategories(variable);
            // Set statistics CONTAINER
            setStatistics(variable);
        }

        *//**
         * Makes this handler ready for merging variable with the given name.
         * Gets a row from ContainerDataField for given variable name or if the row doesn't exist creates it.
         * @param name Variable name
         *//*
        private void start(String name) {
            currentRow = findRowWithFieldValue(container.getRows(), "varname", name);

            // Variable doesn't exist in container, create row
            if(currentRow == null) {
                currentRow = new DataRow(container.getKey(), data.getNewRowId());
                container.getRows().add(currentRow);
                // Set varname using setRowField
                setRowField(currentRow, "varname", name);
            }
        }

        *//**
         * Merge categories container to current row.
         * Creates all missing fields that are needed but uses existing ones if present.
         *
         * @param variable Current variable
         *//*
        private void setCategories(PORUtil.PORVariableHolder variable) {
            ContainerDataField categories = (ContainerDataField)currentRow.getField("categories");
            if(variable.getLabels().size() == 0) {
                if(categories != null) {
                    // Remove all category fields if present by setting their modified value's value to null and adding change
                }
                return;
            }
            // Check to see if we need to initialise categories container
            if(categories == null) {
                categories = new ContainerDataField("categories");
                currentRow.putField(categories);
            }

            List<PORUtil.PORVariableData> variableData = variable.getData();
            Map<String, Integer> map = new HashMap<>();
            groupAnswers(variableData, map);
            for(PORValueLabels vls : variable.getLabels()) {
                for(PORValue v : vls.mappings.keySet()) {
                    DataRow row = findRowWithFieldValue(categories.getRows(), "categoryvalue", v.value);
                    if(row == null) {
                        row = new DataRow(categories.getKey(), data.getNewRowId());
                        categories.getRows().add(row);
                    }
                    setRowField(row, "categoryvalue", v.value);
                    setRowField(row, "categorylabel", vls.mappings.get(v));
                    setRowField(row, "categorystat", (map.containsKey(v.value) ? map.get(v.value).toString() : "0"));
                }
            }
        }

        *//**
         * Group answers in data based on their value.
         * @param variableData Data to be grouped
         * @param map Target map for grouping
         *//*
        private void groupAnswers(List<PORUtil.PORVariableData> variableData, Map<String, Integer> map) {
            for(PORUtil.PORVariableData d : variableData) {
                String key = d.toString();

                if(!map.containsKey(key)) {
                    map.put(key, 0);
                }
                map.put(key, map.get(key)+1);
            }
        }

        private void setStatistics(PORUtil.PORVariableHolder variable) {
            // Get statistics container
            ContainerDataField statistics = (ContainerDataField)currentRow.getField("statistics");
            if(statistics == null) {
                statistics = new ContainerDataField("statistics");
                currentRow.putField(statistics);
            }

            List<PORUtil.PORVariableData> variableData = variable.getNumericalDataWithoutMissing();
            DataRow row;
            String type;
            // Set vald
            Integer variables = variableData.size();
            type = "vald";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", variables.toString());

            // Set min
            if(variables != null && variables > 0) {
                String min = Collections.min(variableData, new PORUtil.PORVariableDataComparator()).toString();
                type = "min";
                row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
                if(row == null) {
                    row = new DataRow(statistics.getKey(), data.getNewRowId());
                    statistics.getRows().add(row);
                    setRowField(row, "statisticstype", type);
                }
                setRowField(row, "statisticsvalue", min);
            }

            // Set max
            if(variables != null && variables > 0) {
                String max = Collections.max(variableData, new PORUtil.PORVariableDataComparator()).toString();
                type = "max";
                row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
                if(row == null) {
                    row = new DataRow(statistics.getKey(), data.getNewRowId());
                    statistics.getRows().add(row);
                    setRowField(row, "statisticstype", type);
                }
                setRowField(row, "statisticsvalue", max);
            }

            // Set mean
            Double mean = 0D;
            Integer meanDenom = 0;
            for(PORUtil.PORVariableData varD : variableData) {
                if(varD instanceof PORUtil.PORVariableDataDouble) {
                    mean += ((PORUtil.PORVariableDataDouble)varD).getValue();
                    meanDenom++;
                } else if(varD instanceof PORUtil.PORVariableDataInt) {
                    mean += ((PORUtil.PORVariableDataInt)varD).getValue();
                    meanDenom++;
                } else if(varD instanceof PORUtil.PORVariableDataString) {
                    mean += Double.parseDouble(((PORUtil.PORVariableDataString)varD).getValue());
                    meanDenom++;
                }
            }
            mean = mean / meanDenom;
            type = "mean";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", mean.toString());

            // Set stdev
            Double deviation = 0D;
            for(PORUtil.PORVariableData varD : variableData) {
                Double value = null;
                if(varD instanceof PORUtil.PORVariableDataDouble) {
                    value = ((PORUtil.PORVariableDataDouble)varD).getValue();
                } else if(varD instanceof PORUtil.PORVariableDataInt) {
                    value = ((PORUtil.PORVariableDataInt)varD).getValue()*1.0;
                } else if(varD instanceof PORUtil.PORVariableDataString) {
                    value = Double.parseDouble(((PORUtil.PORVariableDataString)varD).getValue());
                }
                if(value != null) {
                    deviation += Math.pow((value - mean), 2);
                } else {
                    meanDenom--;
                }
            }
            deviation = Math.sqrt(deviation/meanDenom);

            type = "stdev";
            row = findRowWithFieldValue(statistics.getRows(), "statisticstype", type);
            if(row == null) {
                row = new DataRow(statistics.getKey(), data.getNewRowId());
                statistics.getRows().add(row);
                setRowField(row, "statisticstype", type);
            }
            setRowField(row, "statisticsvalue", deviation.toString());
        }

        *//**
         * Searches through a list of rows for a row containing given value in a field with given id.
         *
         * @param rows List of rows to search through
         * @param key Field key of field where value should be found
         * @param value Value that is searched for
         * @return DataRow that contains given value in requested field, or null if not found.
         *//*
        private DataRow findRowWithFieldValue(List<DataRow> rows, String key, String value) {
            for(DataRow row : rows) {
                SavedDataField field = (SavedDataField)row.getField(key);
                if(field != null && field.hasValue() && field.valueEquals(value)) {
                    return row;
                }
            }
            return null;
        }

        *//**
         * Sets a DataRow field with given key to value provided.
         * If field does not exist then creates the field.
         *
         * TODO: Provide with ChangeContainer
         *
         * @param row Row to be modified
         * @param key Field key
         * @param value Value to be inserted in field
         *//*
        private void setRowField(DataRow row, String key, String value) {
            SavedDataField field = (SavedDataField)row.getField(key);
            if(field == null) {
                field = new SavedDataField(key);
                row.putField(field);
            }
            if(!field.hasValue() || !field.valueEquals(value)) {
                field.setModifiedValue(setSimpleValue(createSavedValue(time), value));
                row.setSavedAt(time);
                // TODO: set savedBy
                // TODO: set change
            }
        }
    }*/
}
