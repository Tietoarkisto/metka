package fi.uta.fsd.metka.storage.variables.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.VariablesFactory;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
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


// TODO: This class is a mess, clean it up
@Repository
public class StudyVariablesParserImpl implements StudyVariablesParser {
    private static Logger logger = LoggerFactory.getLogger(StudyVariablesParserImpl.class);
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private ConfigurationRepository configurations;


    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private RevisionEditRepository edit;

    @Override
    public boolean merge(RevisionData study, VariableDataType type, Configuration studyConfig) {
        long startTime = System.currentTimeMillis();
        boolean result = false;
        // Sanity check
        if(type == null) {
            return result;
        }

        LocalDateTime time = new LocalDateTime();

        // **********************
        // StudyAttachment checks
        // **********************
        // Check that study has attached variables file and get the file id, attaching the file should happen before this step so we can expect it to be present
        SavedDataField field = study.dataField(SavedDataFieldCall.get("variablefile").setConfiguration(studyConfig)).getRight();
        Long varFileId;
        if(field == null || !field.hasValue()) {
            // TODO: Log exception, there is no attached variables file, can't continue.
            return result;
        } else {
            varFileId = SavedDataField.valueAsInteger(field);
        }

        Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(varFileId, false, ConfigurationType.STUDY_ATTACHMENT);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Couldn't find revision data, possibly do something
            return result;
        }
        RevisionData attachmentData = dataPair.getRight();
        // Check for file path from attachment
        field = attachmentData.dataField(SavedDataFieldCall.get("file")).getRight();
        if(field == null || !field.hasValue() || StringUtils.isEmpty(field.getActualValue())) {
            // TODO: Log exception, something is wrong since no path is attached to the file but we are still trying to parse it for variables
            return result;
        }

        // Get or create study variables
        Pair<ReturnResult, Configuration> variablesConfiguration = configurations.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES);
        Pair<StatusCode, SavedDataField> fieldPair = study.dataField(SavedDataFieldCall.get("variables").setConfiguration(studyConfig));
        if(fieldPair.getLeft() == StatusCode.FIELD_MISSING || !fieldPair.getRight().hasValue()) {
            RevisionCreateRequest request = new RevisionCreateRequest();
            request.setType(ConfigurationType.STUDY_VARIABLES);
            request.getParameters().put("studyid", study.getKey().getId().toString());
            request.getParameters().put("fileid", varFileId.toString());
            dataPair = create.create(request);
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                logger.error("Couldn't create new variables revisionable for study "+study.toString()+" and file "+attachmentData.toString());
                return result;
            }
            fieldPair = study.dataField(SavedDataFieldCall.set("variables").setTime(time).setValue(dataPair.getRight().getKey().getId().toString()).setConfiguration(studyConfig));
            result = true;
        } else {
            dataPair = general.getLatestRevisionForIdAndType(Long.parseLong(fieldPair.getRight().getActualValue()), true, ConfigurationType.STUDY_VARIABLES);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                logger.error("Couldn't find revision for study variables with id "+fieldPair.getRight().getActualValue()+" even though it's referenced from study "+study.toString());
                return result;
            }
        }
        RevisionData variablesData = dataPair.getRight();
        if(variablesData.getState() != RevisionState.DRAFT) {
            dataPair = edit.edit(TransferData.buildFromRevisionData(variablesData, RevisionableInfo.FALSE));
            if(dataPair.getLeft() != ReturnResult.REVISION_CREATED) {
                logger.error("Couldn't create new DRAFT revision for "+variablesData.getKey().toString());
                return result;
            }
            variablesData = dataPair.getRight();
        }

        // ************************
        // Actual variables parsing
        // ************************
        // File path to the actual variables file
        field = attachmentData.dataField(SavedDataFieldCall.get("file")).getRight();
        String filePath = field.getActualValue();

        switch(type) {
            case POR:
                result = true;
                // Read POR file
                handlePorVariables(filePath, study.getKey().getId(), variablesData, time);
                break;
        }
        Pair<ReturnResult, String> string = json.serialize(variablesData);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Failed at serializing variables data");
            return result;
        }
        RevisionEntity variablesRevision = em.find(RevisionEntity.class, new RevisionKey(variablesData.getKey().getId(), variablesData.getKey().getNo()));
        variablesRevision.setData(string.getRight());

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
     */
    // TODO: Change this to use create and edit repositories
    private void handlePorVariables(String path, Long studyId, RevisionData variablesData, LocalDateTime time) {
        PORReader reader = new PORReader();
        PORFile por;
        try {
            por = reader.parse(path);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading POR-file with path "+path);
            return;
        }

        // Group variables to list
        List<PORUtil.PORVariableHolder> variables = new ArrayList<>();
        PORUtil.groupVariables(variables, por.variables, por.labels);

        // Group answers under variables list
        PORUtil.PORAnswerMapper visitor = new PORUtil.PORAnswerMapper(variables);
        por.data.accept(visitor);

        // Set software field
        // TODO: look at the possibility of separating software version to different field
        variablesData.dataField(SavedDataFieldCall.set("software").setValue(por.getSoftware()).setTime(time));

        // Set varquantity field
        variablesData.dataField(SavedDataFieldCall.set("varquantity").setValue(por.data.sizeX()+"").setTime(time));

        // Set casequantity field
        variablesData.dataField(SavedDataFieldCall.set("casequantity").setValue(por.data.sizeY()+"").setTime(time));

        // Make VariablesHandler
        VariableHandler handler = new VariableHandler(time, studyId);

        List<StudyVariableEntity> variableEntities =
                em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariablesId=:studyVariablesId", StudyVariableEntity.class)
                        .setParameter("studyVariablesId", variablesData.getKey().getId())
                        .getResultList();

        List<Pair<StudyVariableEntity, PORUtil.PORVariableHolder>> listOfEntitiesAndHolders = new ArrayList<>();
        for(PORUtil.PORVariableHolder variable : variables) {
            StudyVariableEntity variableEntity = null;
            for(Iterator<StudyVariableEntity> i = variableEntities.iterator(); i.hasNext(); ) {
                variableEntity = i.next();
                if(variableEntity.getVariableId().equals(handler.getVariableId(variable))) {
                    i.remove();
                    break;
                }
                variableEntity = null;
            }
            listOfEntitiesAndHolders.add(new ImmutablePair<>(variableEntity, variable));
        }

        ReferenceContainerDataField variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.get("variables")).getRight();

        for(StudyVariableEntity variableEntity : variableEntities) {
            // All remaining rows in variableEntities should be removed since no variable was found for them in the current POR-file

            // If there's an open DRAFT then remove that DRAFT completely
            if(variableEntity.hasDraft()) {
                RevisionEntity revision = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
                if(revision != null) {
                    em.remove(revision);
                }
                variableEntity.setLatestRevisionNo(variableEntity.getCurApprovedNo());
            }

            // Get remaining revisions
            if(variableEntity.getCurApprovedNo() == null) {
                // If there's no approved revision remove variable completely from database
                em.remove(variableEntity);
            } else {
                // Perform a logical removal, i.e. mark STUDY_VARIABLE as removed and mark removal date
                variableEntity.setRemoved(true);
                variableEntity.setRemovalDate(new LocalDateTime());
            }

            // If there is a previous variables container then remove reference from it if present
            if(variablesContainer != null) {
                // See that respective rows are removed from STUDY_VARIABLES
                //    Remove from variables list
                SavedReference reference = variablesContainer.getReferenceWithValue(variableEntity.getId().toString()).getRight();
                reference.remove(variablesData.getChanges());
            }
            //    Remove from variable group list
            // TODO: Handle variable groupings

        }

        // listOfEntitiesAndHolders should contain all variables in the POR-file as well as their existing revisionables. No revisionable is provided if it's a new variable
        if(listOfEntitiesAndHolders.size() > 0 && variablesContainer == null) {
            variablesContainer = variablesData.dataField(ReferenceContainerDataFieldCall.set("variables")).getRight();
        }

        Pair<ReturnResult, Configuration> variableConfiguration = configurations.findLatestConfiguration(ConfigurationType.STUDY_VARIABLE);

        for(Pair<StudyVariableEntity, PORUtil.PORVariableHolder> pair : listOfEntitiesAndHolders) {
            // Iterate through entity/holder pairs. There should always be a holder but missing entity indicates that this is a new variable.
            // After all variables are handled there should be one non removed revisionable per variable in the current por-file.
            // Each revisionable should have an open draft revision (this is a shortcut but it would require doing actual change checking for all variable content to guarantee that no
            // unnecessary revisions are created. This is not required and so a new draft is provided per revisionable).
            // Variables entity should have an open draft revision that includes references to all variables as well as non grouped references for all variables that previously were
            // not in any groups.

            StudyVariableEntity variableEntity = pair.getLeft();
            PORUtil.PORVariableHolder variable = pair.getRight();
            String varId = handler.getVariableId(variable);
            if(variableEntity == null) {
                // New variable
                variableEntity = new StudyVariableEntity();
                variableEntity.setStudyVariablesId(variablesData.getKey().getId());
                variableEntity.setVariableId(varId);
                variableEntity.setStudyId(studyId);
                em.persist(variableEntity);
            }

            // Add Saved reference if missing
            SavedReference reference = variablesContainer.getOrCreateReferenceWithValue(variableEntity.getId().toString(), variablesData.getChanges(), time).getRight();
            // TODO: Add saved reference to ungrouped goupings if not present in any group

            RevisionEntity variableRevision = null;
            VariablesFactory factory = new VariablesFactory();
            if(variableEntity.getLatestRevisionNo() == null) {
                // No initial revision, assume created here and add initial revision
                // TODO: Use revision create repository
                variableRevision = new RevisionEntity(new RevisionKey(variableEntity.getId(), 1));
                Pair<ReturnResult, RevisionData> dataPair = factory.newVariable(variableRevision.getKey().getRevisionableId(), variableRevision.getKey().getRevisionNo(),
                        variableConfiguration.getRight(), variablesData.getKey().getId().toString(), studyId.toString());
                variableRevision.setData(json.serialize(dataPair.getRight()).getRight());
                em.persist(variableRevision);

                variableEntity.setLatestRevisionNo(variableRevision.getKey().getRevisionNo());
            } else if(!variableEntity.hasDraft()) {
                // No draft, should draft be created for merge or can we use approved revision?
                // For now create draft
                RevisionEntity oldVariables = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
                Pair<ReturnResult, RevisionData> oldData = json.deserializeRevisionData(oldVariables.getData());
                if(oldData.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                    logger.error("Failed at deserializing "+oldVariables.toString());
                    continue;
                }
                // TODO: Use revision edit repository
                variableRevision = new RevisionEntity(new RevisionKey(variableEntity.getId(), variableEntity.getLatestRevisionNo()+1));
                RevisionData newData = DataFactory.createDraftRevision(variableRevision.getKey().getRevisionableId(), variableRevision.getKey().getRevisionNo(), oldData.getRight());
                Pair<ReturnResult, String> string = json.serialize(newData);
                if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
                    logger.error("Failed to serialize "+newData.toString());
                    continue;
                }
                variableRevision.setData(string.getRight());

                em.persist(variableRevision);
                variableEntity.setLatestRevisionNo(variableRevision.getKey().getRevisionNo());
            } else {
                // If there is a draft then use that
                variableRevision = em.find(RevisionEntity.class, variableEntity.latestRevisionKey());
            }

            Pair<ReturnResult, RevisionData> variableData = json.deserializeRevisionData(variableRevision.getData());
            if(variableData.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Failed at deserializing "+variableRevision.toString());
                continue;
            }

            // Merge variable to variable revision
            handler.mergeToData(variableData.getRight(), variable);

            // Persis revision with new revision data
            Pair<ReturnResult, String> string = json.serialize(variableData.getRight());
            if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
                logger.error("Failed at serializing "+variableData.getRight().toString());
                continue;
            }
            variableRevision.setData(string.getRight());
        }

        // TODO: After all these steps initiate a re-index on all affected revisions (which can include multiple revisions of one revisionable in the case of logical removal).
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
        private Long studyId;

        VariableHandler(LocalDateTime time, Long studyId) {
            this.time = time;
            this.studyId = studyId;
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
            // TODO: Check return values for problems and changes
            // Set varname field
            variableRevision.dataField(SavedDataFieldCall.set("varname").setValue(variable.asVariable().getName()).setTime(time));
            // Set varid field
            variableRevision.dataField(SavedDataFieldCall.set("varid").setValue(studyId+"_"+variable.asVariable().getName()).setTime(time));
            // Set varlabel field
            String label = StringUtils.isEmpty(StringUtils.trimAllWhitespace(variable.asVariable().label))
                    ? "[Muuttujalta puuttuu LABEL tieto]"
                    : variable.asVariable().label;
            variableRevision.dataField(SavedDataFieldCall.set("varlabel").setValue(label).setTime(time));
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
            ContainerDataField valueLabels = variableRevision.dataField(ContainerDataFieldCall.get("valuelabels")).getRight();

            if(variable.getLabelsSize() == 0 && valueLabels == null) {
                // No labels and no old container. Nothing needs to be done
                return;
            }

            // Check to see if we need to initialise valueLabels container
            // There has to be labels to warrant a valueLabels container creation if it's not present already
            if(variable.getLabelsSize() > 0 && valueLabels == null) {
                valueLabels = variableRevision.dataField(ContainerDataFieldCall.set("valuelabels")).getRight();
            }

            // Gather existing value labels to a separate list to allow for obsolete row checking and preserving por-file defined order
            List<DataRow> rows = new ArrayList<>(valueLabels.getRows());
            valueLabels.getRows().clear();

            // Change map reference since it's used multiple times
            Map<String, Change> changeMap = variableRevision.getChanges();

            // Add container rows
            for(PORUtil.PORVariableValueLabel label : variable.getLabels()) {
                DataRow row = popRowWithFieldValue(rows, "value", label.getValue());
                if(row == null) {
                    row = DataRow.build(valueLabels);
                }
                valueLabels.getRows().add(row);
                setValueLabelRow(row, label, changeMap);
            }

            removeObsoleteRows(rows, valueLabels, changeMap);
        }

        private void setValueLabelRow(DataRow row, PORUtil.PORVariableValueLabel label, Map<String, Change> changeMap) {
            // TODO: Check return values to detect changes and problems
            // We know that this row is needed and so we can set it to not removed state no matter if it was removed previously or not
            row.setRemoved(false);

            row.dataField(SavedDataFieldCall.set("value").setTime(time).setChangeMap(changeMap).setValue(label.getValue()));
            row.dataField(SavedDataFieldCall.set("label").setTime(time).setChangeMap(changeMap).setValue(label.getLabel()));
            row.dataField(SavedDataFieldCall.set("missing").setTime(time).setChangeMap(changeMap).setValue(label.isMissing() ? "Y" : null));
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

            // Get categories container
            ContainerDataField categories = variableRevision.dataField(ContainerDataFieldCall.get("categories")).getRight();
            // Get changeMap reference since it's used multiple times
            Map<String, Change> changeMap = variableRevision.getChanges();

            if(valid.size() == 0 && missing.size() == 0) {
                // No frequencies, remove all frequency rows and return
                if(categories != null) {
                    for(DataRow row : categories.getRows()) {
                        row.remove(changeMap);
                    }
                }
                return;
            }

            // At least some frequencies, check that we have a container
            if(categories == null) {
                categories = variableRevision.dataField(ContainerDataFieldCall.set("categories")).getRight();
            }

            // Gather all old rows
            List<DataRow> rows = new ArrayList<>(categories.getRows());
            categories.getRows().clear();

            // Add valid frequencies
            setFrequencies(variableRevision, variable, categories, valid, changeMap, rows, false);

            // Add missing frequencies
            setFrequencies(variableRevision, variable, categories, missing, changeMap, rows, true);

            // Add SYSMISS if they exist and there are other frequencies, otherwise remove possible existing SYSMISS related inserts
            DataRow sysmissRow = popRowWithFieldValue(rows, "value", "SYSMISS");
            if((valid.size() > 0 || missing.size() > 0) && sysmiss > 0) {
                if(sysmissRow == null) {
                    sysmissRow = DataRow.build(categories);
                }
                categories.getRows().add(sysmissRow);

                setCategoryRow(sysmissRow, "SYSMISS", "SYSMISS", sysmiss, true, time, changeMap);
            } else if(sysmissRow != null) {
                // SYSMISS row is not needed but existed previously, mark as removed
                categories.getRows().add(sysmissRow); // Insert it back to to categories container before removal or the change doesn't make any sense
                sysmissRow.remove(variableRevision.getChanges());
            }
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
        private void setFrequencies(RevisionData revision, PORUtil.PORVariableHolder variable, ContainerDataField target,
                                    Map<PORUtil.PORVariableData, Integer> frequencies, Map<String, Change> changeMap,
                                    List<DataRow> rows, boolean missing) {
            // Add frequencies to target, frequencies map will be empty if frequencies of this type are not required so this step can be passed in that case.
            if(frequencies.size() == 0) {
                return;
            }
            List<PORUtil.PORVariableData> sortedKeys = new ArrayList<>(frequencies.keySet());
            Collections.sort(sortedKeys, new PORUtil.PORVariableDataComparator());
            for(PORUtil.PORVariableData value : sortedKeys) {
                DataRow row = popRowWithFieldValue(rows, "value", value.toString());
                if(row == null) {
                    row = DataRow.build(target);
                }
                target.getRows().add(row);
                PORUtil.PORVariableValueLabel label = variable.getLabel(value.toString());
                Integer freq = frequencies.get(value);

                setCategoryRow(row, value.toString(), (label != null) ? label.getLabel() : null, freq, missing, time, changeMap);
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
            row.setRemoved(false);
            row.dataField(SavedDataFieldCall.set("value").setTime(time).setChangeMap(changeMap).setValue(value));
            row.dataField(SavedDataFieldCall.set("label").setTime(time).setChangeMap(changeMap).setValue(label));
            row.dataField(SavedDataFieldCall.set("categorystat").setTime(time).setChangeMap(changeMap).setValue(stat.toString()));
            row.dataField(SavedDataFieldCall.set("missing").setTime(time).setChangeMap(changeMap).setValue((missing) ? "Y" : null));
        }

        /**
         * Set varinterval attribute for given variable revision.
         *
         * @param variableRevision Variable revision requiring varinterval information
         * @param variable Variable used to determine varinterval value
         */
        private void setInterval(RevisionData variableRevision, PORUtil.PORVariableHolder variable) {
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

            variableRevision.dataField(SavedDataFieldCall.set("varinterval").setValue(continuous ? "contin" : "discrete").setTime(time));
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
            // Get statistics container
            ContainerDataField statistics = variableRevision.dataField(ContainerDataFieldCall.get("statistics")).getRight();
            // Get changeMap
            Map<String, Change> changeMap = variableRevision.getChanges();

            if(!variable.isNumeric()) {
                // Variable is not numeric and should not contain statistics.
                if(statistics != null) {
                    for(DataRow row : statistics.getRows()) {
                        row.remove(changeMap);
                    }
                }
                return;
            }

            // Get valid numerical data as base for calculating statistics
            List<PORUtil.PORVariableDataNumeric> data = variable.getValidNumericalData();
            // amount of valid values is used multiple times so it makes sense to lift it as a separate value
            Integer values = data.size();

            // This variable should have statistics, create if missing
            if(statistics == null) {
                statistics = variableRevision.dataField(ContainerDataFieldCall.set("statistics")).getRight();
            }

            List<DataRow> rows = new ArrayList<>(statistics.getRows());
            statistics.getRows().clear();

            // These variables are needed multiple times so define them separately here
            DataRow row;
            String type;
            String statisticstype = "statisticstype";
            String statisticvalue = "statisticvalue";

            // Set vald
            type = "vald"; // Valid values statistic
            row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
            row.dataField(SavedDataFieldCall.set(statisticvalue).setTime(time).setChangeMap(changeMap).setValue(values.toString()));

            // Set min
            type = "min";
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                String min = Collections.min(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                row.dataField(SavedDataFieldCall.set(statisticvalue).setTime(time).setChangeMap(changeMap).setValue(min));
            }

            // Set max
            type = "max";
            if(values > 0) {
                row = popOrCreateAndInsertRow(statistics, rows, statisticstype, type);
                String max = Collections.max(data, new PORUtil.PORNumericVariableDataComparator()).toString();
                row.dataField(SavedDataFieldCall.set(statisticvalue).setTime(time).setChangeMap(changeMap).setValue(max));
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
                row.dataField(SavedDataFieldCall.set(statisticvalue).setTime(time).setChangeMap(changeMap).setValue(mean.toString()));
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
                row.dataField(SavedDataFieldCall.set(statisticvalue).setTime(time).setChangeMap(changeMap).setValue(deviation.toString()));
            }

            removeObsoleteRows(rows, statistics, changeMap);
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
                SavedDataField field = row.dataField(SavedDataFieldCall.get(key)).getRight();
                if(field != null && field.hasValue() && field.valueEquals(value)) {
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
            target.getRows().add(row);
            return row;
        }

        /**
         * Removes obsolete rows by placing them in given container and then running them through remove method
         * @param rows Collection of rows that are obsolete
         * @param target Container where removed rows are added
         * @param changeMap Change map where target containers changes should be
         */
        private static void removeObsoleteRows(Collection<DataRow> rows, ContainerDataField target, Map<String, Change> changeMap) {
            for(DataRow row : rows) {
                target.getRows().add(row);
                row.remove(changeMap);
            }
        }
    }
}
