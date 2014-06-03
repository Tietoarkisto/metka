package fi.uta.fsd.metka.data.variableParsing.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyAttachmentEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.enums.VariableDataType;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.data.variableParsing.StudyVariablesParser;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spssio.por.PORFile;
import spssio.por.PORMissingValue;
import spssio.por.input.PORReader;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;

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

    @Autowired
    private GeneralRepository general;

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
            attachmentRevision = em.find(RevisionEntity.class, attachmentEntity.latestRevisionKey());
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
            RevisionEntity oldVariables = em.find(RevisionEntity.class, variablesEntity.latestRevisionKey());
            RevisionData oldData = json.readRevisionDataFromString(oldVariables.getData());

            variablesRevision = variablesEntity.createNextRevision();
            variablesRevision.setState(RevisionState.DRAFT);
            RevisionData newData = DataFactory.createNewRevisionData(variablesRevision, oldData);
            variablesRevision.setData(json.serialize(newData));

            em.persist(variablesRevision);
            variablesEntity.setLatestRevisionNo(variablesRevision.getKey().getRevisionNo());
        } else {
            variablesRevision = em.find(RevisionEntity.class, variablesEntity.latestRevisionKey());
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
                handlePorVariables(filePath, variablesData, time);
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
    private void handlePorVariables(String path, RevisionData variablesData, LocalDateTime time) throws IOException {
        PORReader reader = new PORReader();
        PORFile por = reader.parse(path);

        // Group variables to list
        List<PORUtil.PORVariableHolder> variables = new ArrayList<>();
        PORUtil.groupVariables(variables, por.variables, por.labels);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        // Set software field
        // TODO: look at the possibility of separating software version to different field
        SavedDataField field = getSavedDataFieldFromRevisionData(variablesData, "software");
        if(field == null) {
            field = new SavedDataField("software");
            variablesData.putField(field);
        }
        field.setModifiedValue(setSimpleValue(createSavedValue(time), por.getSoftware()));

        // Set varquantity field
        field = getSavedDataFieldFromRevisionData(variablesData, "varquantity");
        if(field == null) {
            field = new SavedDataField("varquantity");
            variablesData.putField(field);
        }
        field.setModifiedValue(setSimpleValue(createSavedValue(time), por.data.sizeX()+""));

        // Set casequantity field
        field = getSavedDataFieldFromRevisionData(variablesData, "casequantity");
        if(field == null) {
            field = new SavedDataField("casequantity");
            variablesData.putField(field);
        }
        field.setModifiedValue(setSimpleValue(createSavedValue(time), por.data.sizeY()+""));

        // Make VariablesHandler
        VariableHandler handler = new VariableHandler(time);

        // Iterate through variables merging data to variables container
        for(PORUtil.PORVariableHolder variable : variables) {
            String varId = handler.getVariableId(variable);
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
                RevisionEntity oldVariables = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
                RevisionData oldData = json.readRevisionDataFromString(oldVariables.getData());

                variableRevision = variableEntity.createNextRevision();
                RevisionData newData = DataFactory.createNewRevisionData(variableRevision, oldData);
                variableRevision.setData(json.serialize(newData));

                em.persist(variableRevision);
                variableEntity.setLatestRevisionNo(variableRevision.getKey().getRevisionNo());
            } else {
                // If there is a draft then use that
                variableRevision = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
            }

            RevisionData variableData = json.readRevisionDataFromString(variableRevision.getData());

            // Merge variable to variable revision
            handler.mergeToData(variableData, variable);

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

        VariableHandler(LocalDateTime time) {
            this.time = time;
        }

        String getVariableId(PORUtil.PORVariableHolder variable) {
            if(variable == null) {
                return null;
            }
            return variable.asVariable().getName();
        }

        /**
         * Handles one PORVariableHolder inserting all relevant information into given variable revision.
         * Handles only fields that are not user editable and so doesn't need to care about not overwriting
         * existing data.
         *
         * @param variableRevision Variable revision where data is inserted
         * @param variable PORVariableHolder containing singe variable data to be parsed
         */
        void mergeToData(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Sanity check
            if(variableRevision == null || variable == null) {
                return;
            }

            // Set varname field
            setSavedDataField(variableRevision, "varname", variable.asVariable().getName(), time);
            // Set varid field
            setSavedDataField(variableRevision, "varid", variable.asVariable().getName(), time);
            // Set varlabel field
            setSavedDataField(variableRevision, "varlabel", StringUtils.isEmpty(StringUtils.trimAllWhitespace(variable.asVariable().label)) ? "[Muuttujalta puuttuu LABEL tieto]" : variable.asVariable().label, time);
            // Set valuelabels CONTAINER
            setValueLabels(variableRevision, variable);
            // Set categories CONTAINER
            // TODO: Categories implementation inaccurate and needs work
            //setCategories(variableRevision, variable);
            // Set statistics CONTAINER
            //setStatistics(variableRevision, variable);
        }

        /**
         * Merge value labels data to given variable revision.
         * Creates missing fields as needed, uses existing ones if present.
         *
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private void setValueLabels(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            ContainerDataField valueLabels = getContainerDataFieldFromRevisionData(variableRevision, "valuelabels");
            ContainerChange labelsChange = (ContainerChange)variableRevision.getChange("valuelabels");

            if(labelsChange == null) {
                labelsChange = new ContainerChange("valuelabels");
            }

            // TODO: Removal of previous entries is still up for debate
            /*if(variable.getLabels().size() == 0) {
                if(valueLabels != null) {
                    if(variableRevision.getChange("valueLabels") == null) {
                        variableRevision.putChange(labelsChange);
                    }
                    // There are no labels in the variable but revision contains categories. Set existing fields to null and insert changes for those fields.
                    for(DataRow row : valueLabels.getRows()) {
                        removeRow(row, labelsChange);
                    }
                }
                return;
            }*/

            // Check to see if we need to initialise categories container
            if(valueLabels == null) {
                valueLabels = new ContainerDataField("valuelabels");
                variableRevision.putField(valueLabels);
            }

            if(variableRevision.getChange("valueLabels") == null) {
                variableRevision.putChange(labelsChange);
            }

            // Add container rows
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                DataRow row = findRowWithFieldValue(valueLabels.getRows(), "value", label.getValue());
                if(row == null) {
                    row = new DataRow(valueLabels.getKey(), variableRevision.getNewRowId());
                    valueLabels.getRows().add(row);
                }

                setRowSavedValue(row, "value", label.getValue(), time, labelsChange);
                setRowSavedValue(row, "label", label.getLabel(), time, labelsChange);

                // Check if label value is user missing value
                if(label.isMissing()) {
                    setRowSavedValue(row, "missing", "Y", time, labelsChange);
                } else {
                    // Check if previous missing assignment was check and reverse it
                    DataField missField = row.getField("missing");
                    // If missing field exists and is SavedDataField set it to null (if it's something else than SavedDataField then do nothing, we have no way of knowing what's going on.
                    if(missField != null && missField instanceof SavedDataField) {
                        // Sets value to null and records a change, if value was previously null then does nothing
                        setRowSavedValue(row, "missing", null, time, labelsChange);
                    }
                }
            }
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
        private void setCategories(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // TODO: Rework to match new rules
            ContainerDataField categories = getContainerDataFieldFromRevisionData(variableRevision, "categories");
            ContainerChange categoriesChange = (ContainerChange)variableRevision.getChange("categories");
            if(categoriesChange == null) {
                categoriesChange = new ContainerChange("categories");
            }

            // TODO: Removal of previous entries is still up for debate
            /*if(variable.getLabels().size() == 0 && variable.getMissing().size() == 0) {
                if(categories != null) {
                    if(variableRevision.getChange("categories") == null) {
                        variableRevision.putChange(categoriesChange);
                    }
                    // There are no labels in the variable but revision contains categories. Remove all existing fields.
                    for(DataRow row : categories.getRows()) {
                        removeRow(row, categoriesChange);
                    }
                }
                return;
            }*/

            // Check to see if we need to initialise categories container
            if(categories == null) {
                categories = new ContainerDataField("categories");
                variableRevision.putField(categories);
            }

            if(variableRevision.getChange("categories") == null) {
                variableRevision.putChange(categoriesChange);
            }

            boolean getValidFreq = false;
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                if(!label.isMissing()) {
                    getValidFreq = true;
                }
                // Break as soon as it becomes apparent that valid frequencies have to be calculated
                if(getValidFreq) {
                    break;
                }
            }

            // Calculate frequencies
            Map<String, Integer> valid = new HashMap<>();
            Map<String, Integer> missing = new HashMap<>();
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
                            increaseFrequencyValue(s.toString(), missing);
                        } else if(getValidFreq) {
                            increaseFrequencyValue(s.toString(), valid);
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
                            increaseFrequencyValue(n.toString(), missing);
                        } else if(getValidFreq) {
                            increaseFrequencyValue(n.toString(), valid);
                        }
                        break;
                }
            }

            // TODO: Depending on the merge tactic it might take more work to make sure that valid values always come after non valid values and in the correct order
            // Add valid values to categories, valid values map will be empty if valid value frequencies are not required and so this step is passed automatically in that case.
            for(Map.Entry<String, Integer> value : valid.entrySet()) {
                DataRow row = findRowWithFieldValue(categories.getRows(), "categoryvalue", value.getKey());
                if(row == null) {
                    row = new DataRow(categories.getKey(), variableRevision.getNewRowId());
                    categories.getRows().add(row);
                }

                setRowSavedValue(row, "value", value.getKey(), time, categoriesChange);
                setRowSavedValue(row, "categorystat", value.getValue().toString(), time, categoriesChange);
                // TODO: Check if value matches a label
                // setRowSavedValue(row, "categorylabel", label.getLabel(), time, categoriesChange);
                PORUtil.PORVariableValueLabel label = variable.getLabel(value.getKey());
                if(label != null) {
                    setRowSavedValue(row, "label", value.getKey(), time, categoriesChange);
                } else {
                    setRowSavedValue(row, "label", null, time, categoriesChange);
                }

                // Clear possible missing value since value is valid.
                // Check if previous missing assignment was check and reverse it
                DataField missField = row.getField("missing");
                if(missField != null && missField instanceof SavedDataField) {
                    // If missing field exists and is SavedDataField set it to null (if it's something else than SavedDataField then do nothing, we have no way of knowing what's going on).
                    setRowSavedValue(row, "missing", null, time, categoriesChange);
                }
            }

            // Add missing values to categories, if there are no missing values then this step is skipped automatically
            for(Map.Entry<String, Integer> value : missing.entrySet()) {
                DataRow row = findRowWithFieldValue(categories.getRows(), "categoryvalue", value.getKey());
                if(row == null) {
                    row = new DataRow(categories.getKey(), variableRevision.getNewRowId());
                    categories.getRows().add(row);
                }

                setRowSavedValue(row, "value", value.getKey(), time, categoriesChange);
                setRowSavedValue(row, "categorystat", value.getValue().toString(), time, categoriesChange);
                // Set missing value since these are values determined to match user missing
                setRowSavedValue(row, "missing", "Y", time, categoriesChange);

                PORUtil.PORVariableValueLabel label = variable.getLabel(value.getKey());
                if(label != null) {
                    setRowSavedValue(row, "label", value.getKey(), time, categoriesChange);
                } else {
                    setRowSavedValue(row, "label", null, time, categoriesChange);
                }
            }

            // Add SYSMISS if they exist, otherwise remove possible existing SYSMISS related inserts
            DataRow sysmissRow = findRowWithFieldValue(categories.getRows(), "categoryvalue", "SYSMISS");
            if(sysmiss > 0) {
                if(sysmissRow == null) {
                    sysmissRow = new DataRow(categories.getKey(), variableRevision.getNewRowId());
                    categories.getRows().add(sysmissRow);
                }
                setRowSavedValue(sysmissRow, "categoryvalue", "SYSMISS", time, categoriesChange);
                setRowSavedValue(sysmissRow, "categorylabel", "SYSMISS", time, categoriesChange);
                setRowSavedValue(sysmissRow, "categorystat", sysmiss+"", time, categoriesChange);
            } else if(sysmissRow != null) {
                // If previous SYSMISS row exists then remove it
                removeRow(sysmissRow, categoriesChange);
            }
        }

        /**
         * Helper function for category checking.
         * Increases an integer value in a map based on a given key. If key doesn't exist previously then puts it to map with value 1
         * @param key
         * @param map
         */
        private void increaseFrequencyValue(String key, Map<String, Integer> map) {
            if(map.containsKey(key)) {
                map.put(key, map.get(key)+1);
            } else {
                map.put(key, 1);
            }
        }

        /**
         * Sets statistics for single variable (min, max etc.)
         * This includes differentiating between cont and discr variables.
         * Mean and deviation are not calculated for all variables
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private void setStatistics(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Get varinterval data field, this should always be present and have a value of either contin or discrete and so we can add it if it's missing
            SavedDataField varinterval = (SavedDataField)variableRevision.getField("varinterval");
            if(varinterval == null) {
                varinterval = new SavedDataField("varinterval");
                variableRevision.putField(varinterval);
                variableRevision.putChange(new Change("varinterval"));
            }
            // Get statistics container
            ContainerDataField statistics = getContainerDataFieldFromRevisionData(variableRevision, "statistics");
            ContainerChange statisticsChange = (ContainerChange)variableRevision.getChange("statistics");
            if(statisticsChange == null) {
                statisticsChange = new ContainerChange("statistics");
                variableRevision.putChange(statisticsChange);
            }
            // Statistics are not calculated for string variables
            // If this variable is string variable then make sure there are no statistics and exit method
            if(!variable.isNumeric()) {
                // Variable is a string variable
                if(statistics != null) {
                    // Clear statistics
                    for(DataRow row : statistics.getRows()) {
                        removeRow(row, statisticsChange);
                    }
                }
                // Set interval to discrete
                setSavedDataField(variableRevision, "varinterval", "discrete", time);
                return;
            }
            List<PORUtil.PORVariableData> data = variable.getNumericalDataWithoutMissing();
            // TODO: Remove user missing values from data
            Integer values = data.size(); // Non SYSMISS numerical data
            if(values == null) values = 0;

            // Set discrete or contin
            if(variable.getLabelsSize() > 0) {
                // TODO: Check for the special case where labels don't cover all possible values
                setSavedDataField(variableRevision, "varinterval", "discrete", time);
            } else {
                setSavedDataField(variableRevision, "varinterval", "discrete", time);
            }

            // This variable should have statistics, create if missing
            if(statistics == null) {
                statistics = new ContainerDataField("statistics");
                variableRevision.putField(statistics);
            }

            // Get all numerical data from this variable, ignore SYSMISS data
            DataRow row;
            String type;
            String statisticstype = "statisticstype";
            String statisticvalue = "statisticvalue";
            // Set vald
            type = "vald"; // Valid values statistic
            row = findRowWithFieldValue(statistics.getRows(), statisticstype, type);
            row = initStatisticsRow(variableRevision, statistics, row, time, statisticstype, type, statisticsChange);
            if(row != null) {
                setRowSavedValue(row, statisticvalue, values.toString(), time, statisticsChange);
            }

            // Set min
            type = "min";
            row = findRowWithFieldValue(statistics.getRows(), statisticstype, type);
            if(values == 0) {
                // clear min by setting row as removed
                removeRow(row, statisticsChange);
            } else {
                row = initStatisticsRow(variableRevision, statistics, row, time, statisticstype, type, statisticsChange);
                if(row != null) {
                    calculateMin(statisticsChange, data, row, statisticvalue);
                }
            }

            // Set max
            type = "max";
            row = findRowWithFieldValue(statistics.getRows(), statisticstype, type);
            if(values == 0) {
                // clear max by setting row as removed
                removeRow(row, statisticsChange);
            } else {
                row = initStatisticsRow(variableRevision, statistics, row, time, statisticstype, type, statisticsChange);
                if(row != null) {
                    calculateMax(statisticsChange, data, row, statisticvalue);
                }
            }

            // Set mean
            type = "mean";
            row = findRowWithFieldValue(statistics.getRows(), statisticstype, type);
            Double mean = 0D;
            // If there are no values or variable is continuous don't add mean
            if(values == 0) {
                // clear mean by setting row as removed
                removeRow(row, statisticsChange);
            } else {
                row = initStatisticsRow(variableRevision, statistics, row, time, statisticstype, type, statisticsChange);
                if(row != null) {
                    mean = calculateMean(statisticsChange, data, row, statisticvalue, mean);
                }
            }

            // Set stdev
            type = "stdev";
            row = findRowWithFieldValue(statistics.getRows(), statisticstype, type);
            // If there are no values or variable is continuous don't add deviation
            if(values == 0) {
                // clear stdev by setting row as removed
                removeRow(row, statisticsChange);
            } else {
                row = initStatisticsRow(variableRevision, statistics, row, time, statisticstype, type, statisticsChange);
                if(row != null) {
                    calculateStandardDeviation(statisticsChange, data, row, statisticvalue, mean);
                }
            }
        }

        private void calculateMin(ContainerChange statisticsChange, List<PORUtil.PORVariableData> data, DataRow row, String statisticvalue) {
            String min = Collections.min(data, new PORUtil.PORVariableDataComparator()).toString();

            setRowSavedValue(row, statisticvalue, min, time, statisticsChange);
        }

        private void calculateMax(ContainerChange statisticsChange, List<PORUtil.PORVariableData> data, DataRow row, String statisticvalue) {
            String max = Collections.max(data, new PORUtil.PORVariableDataComparator()).toString();

            setRowSavedValue(row, statisticvalue, max, time, statisticsChange);
        }

        private Double calculateMean(ContainerChange statisticsChange, List<PORUtil.PORVariableData> data, DataRow row, String statisticvalue, Double mean) {
            Integer denom = 0;
            for(PORUtil.PORVariableData varD : data) {
                if(varD.isNumerical()) {
                    mean += ((PORUtil.PORVariableDataNumeric)varD).getValue();
                    denom++;
                }
            }
            mean = mean / denom;

            setRowSavedValue(row, statisticvalue, mean.toString(), time, statisticsChange);
            return mean;
        }

        private void calculateStandardDeviation(ContainerChange statisticsChange, List<PORUtil.PORVariableData> data, DataRow row, String statisticvalue, Double mean) {
            Double deviation = 0D;
            Integer denom = 0;
            for(PORUtil.PORVariableData varD : data) {
                Double value = null;
                if(varD.isNumerical()) {
                    value = ((PORUtil.PORVariableDataNumeric)varD).getValue();
                }
                if(value != null) {
                    deviation += Math.pow((value - mean), 2);
                    denom++;
                }
            }
            deviation = Math.sqrt(deviation/denom);

            setRowSavedValue(row, statisticvalue, deviation.toString(), time, statisticsChange);
        }

        private DataRow initStatisticsRow(RevisionData data, ContainerDataField container, DataRow row, LocalDateTime time, String typeKey, String type, ContainerChange changeContainer) {
            // Sanity check
            if(data == null || container == null || StringUtils.isEmpty(typeKey) || StringUtils.isEmpty(type) || changeContainer == null) {
                // Can't continue
                return null;
            }
            if(row == null || row.isRemoved()) {
                row = new DataRow(container.getKey(), data.getNewRowId());
                container.getRows().add(row);
                setRowSavedValue(row, typeKey, type, time, changeContainer);
            }
            return row;
        }
    }
}
