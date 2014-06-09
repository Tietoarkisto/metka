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
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataRow;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spssio.por.PORFile;
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
        long startTime = System.currentTimeMillis();
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

        } else if(!variablesEntity.hasDraft()) {
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

        long endTime = System.currentTimeMillis();
        System.err.println("Variable parsing took "+(endTime-startTime)+"ms");
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
        setSavedDataField(variablesData, "software", por.getSoftware(), time);

        // Set varquantity field
        setSavedDataField(variablesData, "varquantity", por.data.sizeX()+"", time);

        // Set casequantity field
        setSavedDataField(variablesData, "casequantity", por.data.sizeY()+"", time);

        // Make VariablesHandler
        VariableHandler handler = new VariableHandler(time);

        // TODO: Update variables list and variables groupings with current information removing references no longer valid

        List<StudyVariableEntity> variableEntities =
                em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariablesId=:studyVariablesId", StudyVariableEntity.class)
                        .setParameter("studyVariablesId", variablesData.getKey().getId())
                        .getResultList();

        List<Pair<StudyVariableEntity, PORUtil.PORVariableHolder>> listOfEntitiesAndHolders = new ArrayList<>();
        Iterator<StudyVariableEntity> iter;
        for(PORUtil.PORVariableHolder variable : variables) {
            iter = variableEntities.iterator();
            StudyVariableEntity variableEntity = null;
            while(iter.hasNext()) {
                variableEntity = iter.next();
                if(variableEntity.getVariableId().equals(handler.getVariableId(variable))) {
                    iter.remove();
                    break;
                }
                variableEntity = null;
            }
            listOfEntitiesAndHolders.add(new ImmutablePair<>(variableEntity, variable));
        }

        for(StudyVariableEntity variableEntity : variableEntities) {
            // All remaining rows in variableEntities should be removed since no variable was found for them in the current POR-file

            // See that respective rows are removed from STUDY_VARIABLES
            //    Remove from variables list
            //    Remove from variable group list
            //
            // If there's an open DRAFT then remove that DRAFT completely
            if(variableEntity.hasDraft()) {
                RevisionEntity revision = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
                if(revision != null) {
                    em.remove(revision);
                }
                variableEntity.setLatestRevisionNo(variableEntity.getCurApprovedNo());
            }

            // Get remaining revisions
            List<RevisionEntity> revisions = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId = :id", RevisionEntity.class)
                    .setParameter("id", variableEntity.getId())
                    .getResultList();

            if(revisions.size() == 0) {
                // If there's no approved revision remove variable completely from database
                em.remove(variableEntity);
            } else {
                // Perform a logical removal, i.e. mark STUDY_VARIABLE as removed and mark removal date
                variableEntity.setRemoved(true);
                variableEntity.setRemovalDate(new LocalDateTime());
            }

            // TODO: After all these steps initiate a re-index on all affected revisions (which can include multiple revisions of one revisionable in the case of logical removal).
        }

        // listofEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable

        for(Pair<StudyVariableEntity, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            // Iterate through entity/holder pairs. There should always be a holder but missing entity indicates that this is a new variable.
            // After all variables are handled there should be one non removed revisionable per variable in the current por-file.
            // Each revisionable should have an open draft revision (this is a shortcut but it would require doing actual change checking for all variable content to guarantee that no
            // unnecessary revisions are created. This is not required and so a new draft is provided per revisionable).
            // Variables entity should have an open draft revision that includes references to all variables as well as non grouped references for all variables that previously were
            // not in any groups.

            // TODO: Actual implementation
        }

        // Iterate through variables merging data to variables container
        for(PORUtil.PORVariableHolder variable : variables) {
            String varId = handler.getVariableId(variable);
            // Look for matching variable revisionable.
            /*List<StudyVariableEntity> variableEntities =
                    em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariablesId=:studyVariablesId AND e.variableId=:variableId", StudyVariableEntity.class)
                    .setParameter("studyVariablesId", variablesData.getKey().getId())
                    .setParameter("variableId", varId)
                    .getResultList();*/

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
            } else if(!variableEntity.hasDraft()) {
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
            setCategories(variableRevision, variable);
            // Set interval
            setInterval(variableRevision, variable);
            // Set statistics CONTAINER
            setStatistics(variableRevision, variable);
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

            // Check to see if we need to initialise valueLabels container
            // There has to be labels to warrant a valueLabels container creation if it's not present already
            if(variable.getLabelsSize() > 0 && valueLabels == null) {
                valueLabels = new ContainerDataField("valuelabels");
                variableRevision.putField(valueLabels);
            }

            // Change map reference since it's used multiple times
            Map<String, Change> changeMap = variableRevision.getChanges();

            // Add container rows
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                DataRow row = findOrCreateRowWithFieldValue(variableRevision, valueLabels, "value", label.getValue(), changeMap, time);

                setRowSavedValue(row, "value", label.getValue(), time, changeMap);
                setRowSavedValue(row, "label", label.getLabel(), time, changeMap);
                setRowSavedValue(row, "missing", (label.isMissing() ? "Y" : null), time, changeMap);
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

            // Check to see if we need to initialise categories container
            // There has to be at least some valid or missing frequencies to warrant
            if((valid.size() > 0 || missing.size() > 0) && categories == null) {
                categories = new ContainerDataField("categories");
                variableRevision.putField(categories);
            }

            // Get changeMap reference since it's used multiple times
            Map<String, Change> changeMap = variableRevision.getChanges();

            // TODO: Depending on the merge tactic it might take more work to make sure that valid values always come after non valid values and in the correct order
            // Add valid values to categories, valid values map will be empty if valid value frequencies are not required and so this step is passed automatically in that case.
            List<PORUtil.PORVariableData> sortedKeys = new ArrayList<>(valid.keySet());
            Collections.sort(sortedKeys, new PORUtil.PORVariableDataComparator());
            for(PORUtil.PORVariableData value : sortedKeys) {
                DataRow row = findOrCreateRowWithFieldValue(variableRevision, categories, "value", value.toString(), changeMap, time);
                PORUtil.PORVariableValueLabel label = variable.getLabel(value.toString());
                Integer freq = valid.get(value);

                setCategoryRow(row, value.toString(), (label != null) ? label.getLabel() : null, freq, false, time, changeMap);
            }

            // Add missing values to categories, if there are no missing values then this step is skipped automatically
            sortedKeys = new ArrayList<>(missing.keySet());
            Collections.sort(sortedKeys, new PORUtil.PORVariableDataComparator());
            for(PORUtil.PORVariableData value : sortedKeys) {
                DataRow row = findOrCreateRowWithFieldValue(variableRevision, categories, "value", value.toString(), changeMap, time);
                PORUtil.PORVariableValueLabel label = variable.getLabel(value.toString());
                Integer freq = missing.get(value);

                setCategoryRow(row, value.toString(), (label != null) ? label.getLabel() : null, freq, true, time, changeMap);
            }

            // Categories container has to be present for SYSMISS value checking to make sense
            if(categories != null) {
                // Add SYSMISS if they exist and there are other frequencies, otherwise remove possible existing SYSMISS related inserts
                DataRow sysmissRow = (categories != null) ? findRowWithFieldValue(categories.getRows(), "value", "SYSMISS") : null;
                if((valid.size() > 0 || missing.size() > 0) && sysmiss > 0) {
                    if(sysmissRow == null) {
                        sysmissRow = new DataRow(categories.getKey(), variableRevision.getNewRowId());
                        categories.putRow(sysmissRow);
                    }

                    setCategoryRow(sysmissRow, "SYSMISS", "SYSMISS", sysmiss, true, time, changeMap);
                } else if(sysmissRow != null) {
                    // If previous SYSMISS row exists then remove it
                    removeRow(sysmissRow, variableRevision.getChanges());
                }
            }
        }

        /**
         * Helper function for category checking.
         * Increases an integer value in a map based on a given key. If key doesn't exist previously then puts it to map with value 1
         * @param key
         * @param map
         */
        private void increaseFrequencyValue(PORUtil.PORVariableData key, Map<PORUtil.PORVariableData, Integer> map) {
            if(map.containsKey(key)) {
                map.put(key, map.get(key)+1);
            } else {
                map.put(key, 1);
            }
        }

        /**
         * Helper function for setting category row values
         * @param row Row where the values are set
         * @param value Category value for the row
         * @param label Possible label for the row, can be null
         * @param stat Frequency statistic for the row
         * @param missing Is the category a missing category or not
         * @param time Time instance used for any changes
         * @param changeMap Where changes are logged
         */
        private void setCategoryRow(DataRow row, String value, String label, Integer stat, boolean missing, LocalDateTime time, Map<String, Change> changeMap) {
            setRowSavedValue(row, "value", value, time, changeMap);
            setRowSavedValue(row, "label", label, time, changeMap);
            setRowSavedValue(row, "categorystat", stat.toString(), time, changeMap);
            // Set missing value since these are values determined to match user missing
            setRowSavedValue(row, "missing", (missing) ? "Y" : null, time, changeMap);
        }

        /**
         * Set varinterval attribute for given variable revision.
         *
         * @param variableRevision Variable revision requiring varinterval information
         * @param variable Variable used to determine varinterval value
         */
        private void setInterval(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            // Start from the assumption that the variable is continuous and work from there
            boolean isContin = true;

            if(!variable.isNumeric()) {
                isContin = false;
            }

            if(isContin && variable.getLabelsSize() > 0) {
                for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                    if(!label.isMissing()) {
                        isContin = false;
                        break;
                    }
                }
            }

            setSavedDataField(variableRevision, "varinterval", (isContin ? "contin" : "discrete"), time);
        }

        /**
         * Sets statistics for single variable (min, max etc.)
         * Statistics are calculated only for numerical variables and all statistics are calculated for all numerical variables.
         * SYSMISS and user missing values are not valid values for statistics
         *
         * @param variableRevision Variable revision to merge variable data to.
         * @param variable Current variable
         */
        private void setStatistics(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
            if(!variable.isNumeric()) {
                // Variable is not numeric and should not contain statistics.

                // TODO: removal of previous entries during merge is still up for debate

                return;
            }

            // Get statistics container
            ContainerDataField statistics = getContainerDataFieldFromRevisionData(variableRevision, "statistics");

            // Get valid numerical data as base for calculating statistics
            List<PORUtil.PORVariableDataNumeric> data = variable.getValidNumericalData();
            // amount of valid values is used multiple times so it makes sense to lift it as a separate value
            Integer values = data.size();

            // This variable should have statistics, create if missing
            if(statistics == null) {
                statistics = new ContainerDataField("statistics");
                variableRevision.putField(statistics);
            }

            // These variables are needed multiple times so define them separately here
            DataRow row;
            String type;
            String statisticstype = "statisticstype";
            String statisticvalue = "statisticvalue";
            Map<String, Change> changeMap = variableRevision.getChanges();

            // Set vald
            type = "vald"; // Valid values statistic
            row = findOrCreateRowWithFieldValue(variableRevision, statistics, statisticstype, type, changeMap, time);
            setRowSavedValue(row, statisticvalue, values.toString(), time, changeMap);

            // Set min
            type = "min";
            if(values == 0) {
                // clear min by setting row as removed
                removeRow(findRowWithFieldValue(statistics.getRows(), statisticstype, type), changeMap);
            } else {
                row = findOrCreateRowWithFieldValue(variableRevision, statistics, statisticstype, type, changeMap, time);
                String min = Collections.min(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                setRowSavedValue(row, statisticvalue, min, time, changeMap);
            }

            // Set max
            type = "max";
            if(values == 0) {
                // clear max by setting row as removed
                removeRow(findRowWithFieldValue(statistics.getRows(), statisticstype, type), changeMap);
            } else {
                row = findOrCreateRowWithFieldValue(variableRevision, statistics, statisticstype, type, changeMap, time);
                String max = Collections.max(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                setRowSavedValue(row, statisticvalue, max, time, changeMap);
            }

            // Set mean
            type = "mean";
            Double mean = 0D;
            // If there are no values or variable is continuous don't add mean
            if(values == 0) {
                // clear mean by setting row as removed
                removeRow(findRowWithFieldValue(statistics.getRows(), statisticstype, type), changeMap);
            } else {
                row = findOrCreateRowWithFieldValue(variableRevision, statistics, statisticstype, type, changeMap, time);
                Integer denom = 0;
                for(PORUtil.PORVariableDataNumeric varD : data) {
                    mean += varD.getValue();
                    denom++;
                }
                mean = mean / denom;
                setRowSavedValue(row, statisticvalue, mean.toString(), time, changeMap);
            }

            // Set stdev
            type = "stdev";
            // If there are no values or variable is continuous don't add deviation
            if(values == 0) {
                // clear stdev by setting row as removed
                removeRow(findRowWithFieldValue(statistics.getRows(), statisticstype, type), changeMap);
            } else {
                row = findOrCreateRowWithFieldValue(variableRevision, statistics, statisticstype, type, changeMap, time);
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
                setRowSavedValue(row, statisticvalue, deviation.toString(), time, changeMap);
            }
        }
    }
}
