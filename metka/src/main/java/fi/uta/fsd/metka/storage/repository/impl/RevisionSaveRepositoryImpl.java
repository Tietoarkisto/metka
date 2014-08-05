package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.FieldError;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.TransferFieldType;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionSaveRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

@Repository
public class RevisionSaveRepositoryImpl implements RevisionSaveRepository {
    private static Logger logger = LoggerFactory.getLogger(RevisionSaveRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private GeneralRepository general;

    @Override
    public Pair<ReturnResult, TransferData> saveRevision(TransferData transferData) {
        Pair<ReturnResult, RevisionData> revisionPair = general.getRevisionDataOfType(transferData.getKey().getId(), transferData.getKey().getNo(), transferData.getConfiguration().getType());
        if(revisionPair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Couldn't find Revision "+transferData.getKey().toString()+" while saving.");
            return new ImmutablePair<>(revisionPair.getLeft(), transferData);
        }

        RevisionData revision = revisionPair.getRight();
        if(revision.getState() != RevisionState.DRAFT) {
            logger.warn("Revision "+revision.toString()+" was not in DRAFT state when tried to initiate save");
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_A_DRAFT, transferData);
        }

        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            logger.error("Couldn't find configuration "+revision.getConfiguration().toString()+" while saving "+revision.toString());
            return new ImmutablePair<>(configPair.getLeft(), transferData);
        }

        Configuration configuration = configPair.getRight();

        // Create DateTime object for updating values
        LocalDateTime time = new LocalDateTime();

        // Do actual change checking for field values
        Pair<Boolean, Boolean> changesAndErrors = saveFields(configuration, transferData, revision, time);

        // TODO: Do CONCAT checking

        // TODO: Do type specific operations if any

        if(changesAndErrors.getLeft()) {
            // Set revision save info
            revision.setLastSaved(time);
            // TODO: Set last saved by


            ReturnResult result = general.updateRevisionData(revision);
            if(result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, transferData);
            } else {
                // Set transfer object save info since database values have changed
                transferData.getState().setSaved(true);
                transferData.getState().setSavedDate(time);
                transferData.getState().setSavedBy(revision.getLastSavedBy());
                return new ImmutablePair<>((changesAndErrors.getRight() ? ReturnResult.SAVE_SUCCESSFUL_WITH_ERRORS : ReturnResult.SAVE_SUCCESSFUL), transferData);
            }
        } else {
            return new ImmutablePair<>(ReturnResult.NO_CHANGES_TO_SAVE, transferData);
        }
    }

    private Pair<Boolean, Boolean> saveFields(Configuration configuration, TransferData transferData, RevisionData revisionData, LocalDateTime time) {
        MutablePair<Boolean, Boolean> result = new MutablePair<>(false, false);
        // Loop through all fields, skip fields that are irrelevant or checked later and call correct methods to save DataFields

        for(Field field : configuration.getFields().values()) {
            if(!field.getWritable()) {
                // Field is not writable, don't check for changes and don't add to RevisionData
                continue;
            } else if(field.getSubfield()) {
                // No need to process further, subfields are handled when their containers are processed
                continue;
            }
            Pair<StatusCode, Boolean> saveResult = saveField(field, configuration, transferData, revisionData, revisionData.getChanges(), time);
            if(saveResult.getLeft() == StatusCode.FIELD_CHANGED && !result.getLeft()) result.setLeft(true);
            if(saveResult.getRight() && !result.getRight()) result.setRight(true);
        }
        return result;
    }

    private Pair<StatusCode, Boolean> saveField(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap, LocalDateTime time) {
        Pair<StatusCode, Boolean> returnPair;
        if(field.getType() == FieldType.CONTAINER) {
            returnPair = saveContainer(field, configuration, transferFields, dataFields, changeMap, time);
        } else if(field.getType() == FieldType.REFERENCECONTAINER) {
            returnPair = saveReferenceContainer(field, configuration, transferFields, dataFields, changeMap, time);
        } else {
            returnPair = saveValue(field, configuration, transferFields, dataFields, changeMap);
        }
        return returnPair;
    }

    /**
     * Saves changes to single ContainerDataField in provided DataFieldContainer from single CONTAINER type TransferField in provided TransferFieldContainer.
     * This can lead to recursive calls if needed.
     *
     * @param field Field configuration of the field to be saved
     * @param configuration Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
     * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
     * @param dataFields DataFieldContainer that should contain the ContainerDataField described by Field configuration
     * @param changeMap Map of changes that should contain changes for field being checked
     * @return Pair<StatusCode, Boolean> statusAndErrors pair. Left value indicates the returned status of the final operation performed, right value indicates that errors were marked somewhere within the TransferFields
     */
    private Pair<StatusCode, Boolean> saveContainer(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap, LocalDateTime time) {
        TransferField tf = transferFields.getField(field.getKey());
        if(tf != null && tf.getType() != TransferFieldType.CONTAINER) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        }
        Pair<StatusCode, ContainerDataField> codePair = dataFields.dataField(ContainerDataFieldCall.get(field.getKey()).setConfiguration(configuration));
        if(codePair.getLeft() == StatusCode.FIELD_FOUND && tf == null) {
            // Existing container is missing from TransferFields, client should never remove whole containers after they are created
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        } else if(codePair.getLeft() == StatusCode.FIELD_MISSING && tf == null) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
        }
        if(codePair.getLeft() != StatusCode.FIELD_FOUND && (tf == null || tf.getRows().size() == 0)) {
            return new ImmutablePair<>(codePair.getLeft(), false);
        }
        if(codePair.getLeft() == StatusCode.FIELD_FOUND && codePair.getRight().getRows().size() != 0 && tf.getRows().size() == 0) {
            // Rows should never be removed like this but instead by marking them as removed
            // Mark error to transfer field and do no changes
            tf.getErrors().add(FieldError.MISSING_ROWS);
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, true);
        }

        if(codePair.getLeft() == StatusCode.FIELD_MISSING) {
            codePair = dataFields.dataField(ContainerDataFieldCall.set(field.getKey()).setConfiguration(configuration));
            if(codePair.getLeft() != StatusCode.FIELD_INSERT) {
                return new ImmutablePair<>(codePair.getLeft(), false);
            }
        }
        // Return value tracker for individual rows
        MutablePair<StatusCode, Boolean> returnPair = new MutablePair<>(StatusCode.FIELD_UPDATE, false);
        ContainerDataField container = codePair.getRight();
        boolean changes = false;
        // Check that all rows that exist are present
        for(DataRow row : container.getRows()) {
            boolean found = false;
            for(TransferRow tr : tf.getRows()) {
                if(row.getRowId().equals(tr.getRowId())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                // There is an existing row missing from TransferField, mark error and return value
                tf.getErrors().add(FieldError.MISSING_ROWS);
                returnPair.setRight(true);
                break;
            }
        }

        ContainerChange containerChange = (ContainerChange)changeMap.get(field.getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(field.getKey());
        }
        // Regardless of if there are missing rows we are going to save the changes in existing and new rows
        // but no rows are going to be deleted because of them missing from TransferField
        for(TransferRow tr : tf.getRows()) {
            DataRow row = null;
            if(tr.getRowId() == null && tr.getFields().size() > 0) {
                // New row that has some fields in it. Create row in preparation for saving
                Pair<StatusCode, DataRow> rowPair = container.insertNewDataRow(changeMap);
                if(rowPair.getLeft() == StatusCode.NEW_ROW) {
                    row = rowPair.getRight();
                    changes = true;
                    tr.setRowId(row.getRowId());
                }
            } else {
                // Old row, check the "removed" value.
                Pair<StatusCode, DataRow> rowPair = container.getRowWithId(tr.getRowId());
                if(rowPair.getLeft() != StatusCode.FOUND_ROW) {
                    tr.getErrors().add(FieldError.ROW_NOT_FOUND);
                    returnPair.setRight(true);
                    continue;
                }
                row = rowPair.getRight();
                if(tr.getRemoved() != row.isRemoved()) {
                    // Removed value of a row was changed
                    // TODO: If someone is trying to reinstate a removed row then check that they have necessary role
                    row.setRemoved(tr.getRemoved());
                    row.setSavedAt(time);
                    // TODO: set saved by
                    changes = true;
                }
            }
            if(row != null) {
                // Row was found, go through the subfields and forward calls to field saving
                for(String subkey : field.getSubfields()) {
                    Field subfield = configuration.getField(subkey);
                    if(subfield == null) {
                        logger.error("Didn't find field "+subkey+" in configuration "+configuration.getKey().toString()+" even though "+field.getKey()+" has it as subfield.");
                        continue;
                    }
                    RowChange rowChange = containerChange.get(row.getRowId());
                    if(rowChange == null) {
                        rowChange = new RowChange(row.getRowId());
                    }

                    // Call save field for the subfield, this can cause recursion
                    Pair<StatusCode, Boolean> fieldSaveResult = saveField(subfield, configuration, tr, row, rowChange.getChanges(), time);

                    if(fieldSaveResult.getRight()) {
                        returnPair.setRight(true);
                    }
                    if(fieldSaveResult.getLeft() == StatusCode.FIELD_CHANGED) {
                        // Make sure that row change is in the container change since there has been an actual change.
                        containerChange.put(rowChange);
                        changes = true;
                    }
                }
            }
        }


        if(changes) {
            changeMap.put(containerChange.getKey(), containerChange);
            returnPair.setLeft(StatusCode.FIELD_CHANGED);
        } else {
            returnPair.setLeft(StatusCode.NO_CHANGE_IN_VALUE);
        }
        return returnPair;
    }

    /**
     * Saves changes to single ReferenceContainerDataField in provided DataFieldContainer from single REFERENCECONTAINER type TransferField in provided TransferFieldContainer.
     *
     * @param field Field configuration of the field to be saved
     * @param configuration Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
     * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
     * @param dataFields DataFieldContainer that should contain the ReferenceContainerDataField described by Field configuration
     * @param changeMap Map of changes that should contain changes for field being checked
     * @return Pair<StatusCode, Boolean> statusAndErrors pair. Left value indicates the returned status of the final operation performed, right value indicates that errors were marked somewhere within the TransferFields
     */
    private Pair<StatusCode, Boolean> saveReferenceContainer(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap, LocalDateTime time) {
        TransferField tf = transferFields.getField(field.getKey());
        if(tf != null && tf.getType() != TransferFieldType.REFERENCECONTAINER) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        }
        Pair<StatusCode, ReferenceContainerDataField> codePair = dataFields.dataField(ReferenceContainerDataFieldCall.get(field.getKey()).setConfiguration(configuration));
        if(codePair.getLeft() == StatusCode.FIELD_FOUND && tf == null) {
            // Existing reference container is missing from TransferFields, client should never remove whole containers after they are created
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        } else if(codePair.getLeft() == StatusCode.FIELD_MISSING && tf == null) {
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
        }
        if(codePair.getLeft() != StatusCode.FIELD_FOUND && (tf == null || tf.getRows().size() == 0)) {
            return new ImmutablePair<>(codePair.getLeft(), false);
        }
        if(codePair.getLeft() == StatusCode.FIELD_FOUND && codePair.getRight().getReferences().size() != 0 && (tf == null || tf.getRows().size() == 0)) {
            // Rows should never be removed like this but instead by marking them as removed
            if(tf != null) {
                // Mark error to transfer field and do no changes
                tf.getErrors().add(FieldError.MISSING_ROWS);
            }
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, true);
        }

        if(tf == null) {
            // We are at a point where TransferField has to be non null, if it's not then return
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        }

        if(codePair.getLeft() == StatusCode.FIELD_MISSING) {
            codePair = dataFields.dataField(ReferenceContainerDataFieldCall.set(field.getKey()).setConfiguration(configuration));
            if(codePair.getLeft() != StatusCode.FIELD_INSERT) {
                return new ImmutablePair<>(codePair.getLeft(), false);
            }
        }
        // Return value tracker for individual rows
        MutablePair<StatusCode, Boolean> returnPair = new MutablePair<>(StatusCode.FIELD_UPDATE, false);
        boolean changes = false;
        ReferenceContainerDataField container = codePair.getRight();
        // Check that all rows that exist are present
        for(SavedReference reference : container.getReferences()) {
            boolean found = false;
            for(TransferRow tr : tf.getRows()) {
                if(reference.getRowId().equals(tr.getRowId())) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                // There is an existing row missing from TransferField, mark error and return value
                tf.getErrors().add(FieldError.MISSING_ROWS);
                returnPair.setRight(true);
                break;
            }
        }
        // Regardless of if there are missing rows we are going to save the changes in existing and new rows
        // but no rows are going to be deleted because of them missing from TransferField
        for(TransferRow tr : tf.getRows()) {
            if(StringUtils.isEmpty(tr.getValue())) {
                // There should always be a value on existing row. If value is missing add error, set return value and skip this row. Saved reference value is immutable so nothing gets deleted through setting the value to null.
                tr.getErrors().add(FieldError.MISSING_VALUE);
                returnPair.setRight(true);
            } else if(tr.getRowId() == null) {
                // New row, insert new SavedReference
                Pair<StatusCode, SavedReference> referencePair = container.getOrCreateReferenceWithValue(tr.getValue(), changeMap, time);
                if(referencePair.getLeft() == StatusCode.NEW_ROW) {
                    changes = true;
                    tr.setRowId(referencePair.getRight().getRowId());
                }
            } else {
                // Old row, the only thing that can change is "removed". The actual reference value on SavedReference is immutable
                // and is locked in when the row is created
                Pair<StatusCode, SavedReference> referencePair = container.getReferenceWithId(tr.getRowId());
                if(referencePair.getLeft() != StatusCode.FOUND_ROW) {
                    tr.getErrors().add(FieldError.ROW_NOT_FOUND);
                    returnPair.setRight(true);
                    continue;
                }
                SavedReference reference = referencePair.getRight();
                if(!reference.valueEquals(tr.getValue())) {
                    // Something tried to change the value, add error, set return value and continue
                    tr.getErrors().add(FieldError.IMMUTABLE);
                    returnPair.setRight(true);
                    continue;
                }
                if(tr.getRemoved() != reference.isRemoved()) {
                    // Removed value of a row was changed
                    // TODO: If someone is trying to reinstate a removed row then check that they have necessary role
                    reference.setRemoved(tr.getRemoved());
                    changes = true;
                }
            }
        }

        if(changes) {
            returnPair.setLeft(StatusCode.FIELD_CHANGED);
        } else {
            returnPair.setLeft(StatusCode.NO_CHANGE_IN_VALUE);
        }
        return returnPair;
    }

    /**
     * Saves changes to single SavedDataField in provided DataFieldContainer from single VALUE type TransferField in provided TransferFieldContainer.
     *
     * @param field Field configuration of the field to be saved
     * @param configuration Configuration of the RevisionData being handled. This is needed for SELECTION fields, REFERENCEs etc.
     * @param transferFields TransferFieldContainer that should contain the TransferField described by Field configuration
     * @param dataFields DataFieldContainer that should contain the SavedDataField described by Field configuration
     * @param changeMap Map of changes that should contain changes for field being checked
     * @return Pair<Boolean, Boolean> changesAndErrors pair. Left value indicates that changes have taken place, right value indicates that errors were marked somewhere within the TransferFields
     */
    private Pair<StatusCode, Boolean> saveValue(Field field, Configuration configuration, TransferFieldContainer transferFields, DataFieldContainer dataFields, Map<String, Change> changeMap) {
        if(field.getType() == FieldType.CONCAT) {
            // Concat type fields are not saved here but instead must be handled separately after all other fields have been saved
            return new ImmutablePair<>(StatusCode.NO_CHANGE_IN_VALUE, false);
        }
        TransferField tf = transferFields.getField(field.getKey());
        if(tf != null && tf.getType() != TransferFieldType.VALUE) {
            return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, false);
        }
        String value = (tf != null && tf.getValue() != null) ? tf.getValue().getCurrent() : null;
        Pair<StatusCode, SavedDataField> codePair = dataFields.dataField(SavedDataFieldCall.check(field.getKey()).setValue(value).setConfiguration(configuration));
        StatusCode statusCode = codePair.getLeft();
        if(!(statusCode == StatusCode.FIELD_INSERT || statusCode == StatusCode.FIELD_UPDATE)) {
            boolean errors = false;
            if(tf != null) {
                switch(statusCode) {
                    case FIELD_NOT_MUTABLE:
                        tf.getErrors().add(FieldError.IMMUTABLE);
                        errors = true;
                        break;
                    case FIELD_NOT_EDITABLE:
                        tf.getErrors().add(FieldError.NOT_EDITABLE);
                        errors = true;
                        break;
                    default:
                        break;
                }
            }
            return new ImmutablePair<>(statusCode, errors);
        }

        codePair = dataFields.dataField(SavedDataFieldCall.set(field.getKey()).setValue(value).setConfiguration(configuration));
        statusCode = codePair.getLeft();
        if(statusCode == StatusCode.FIELD_INSERT || statusCode == StatusCode.FIELD_UPDATE) {
            statusCode = StatusCode.FIELD_CHANGED;
        }
        return new ImmutablePair<>(statusCode, false);
    }
}
