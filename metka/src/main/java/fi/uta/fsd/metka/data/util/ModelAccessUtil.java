package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;
import static fi.uta.fsd.metka.data.util.ModelValueUtil.*;

// TODO: Refactor to use ModelFieldUtil and move away from JSONObject
public final class ModelAccessUtil {
    // Private constructor to stop instantiation
    private ModelAccessUtil() {}

    public static interface PathNavigable {

    }
    /**
     * Returns a DataField using field path navigation.
     * If supplied path doesn't lead to a legal DataField then null is returned.
     * Since both Containers and Rows are also DataFields both can be returned with this method.
     *
     * Field path consist of one or more steps separated by period.
     * Each step is either a field key that should be found from given position or an integer denoting a rowId for
     * specific row.
     * @param path Path to requested DataField
     * @param data RevisionData from which the DataField should be returned.
     * @return DataField found from the specific path.
     */
    public static PathNavigable getFieldDotFormat(String path, RevisionData data) {
        List<String> steps = Arrays.asList(path.split("."));
        PathNavigable field = null;
        for(String step : steps) {
            if(field == null) {
                field = data.getField(step);
                if(field == null) {
                    // Path terminates here, no starting field found. Return null.
                    return null;
                }
            } else {
                if(field instanceof ContainerDataField) {
                    ContainerDataField container = (ContainerDataField)field;
                    Integer rowId = stringToInteger(step);
                    if(rowId != null) {
                        field = null;
                        for(DataRow row : container.getRows()) {
                            if(row.getRowId().equals(rowId)) {
                                // Found correct row, break.
                                field = row;
                                break;
                            }
                        }
                        if(field == null) {
                            // No correct row found, path terminates here, return null.
                            return null;
                        }
                    } else {
                        // Path is invalid, we should find a row but step is not an integer.
                        return null;
                    }
                } else if(field instanceof DataRow) {
                    DataRow row = (DataRow)field;
                    field = row.getField(step);
                    if(field == null) {
                        // Path terminates here, no matter if there are more steps. Return null.
                        return null;
                    }
                }
                // TODO: ReferenceContainerDataField
            }
        }
        return field;
    }

    public static boolean idIntegrityCheck(TransferObject to, RevisionData data, Configuration config) {
        String key = config.getIdField();
        SavedDataField field = data.dataField(SavedDataFieldCall.get(key).setConfiguration(config)).getRight();
        if(field == null) {
            // TODO: tried to check id integrity with Container field. Container field should never be id field. Log error
            // Tried to check integrity with wrong field, integrity can not be checked.
            return false;
        }
        Long dataIdField = extractIntegerSimpleValue(field);
        Long toIdField = stringToLong(to.getByKey(key));
        if(dataIdField == null) {
            // TODO: somehow id data has gone missing, log error
            // Return false since save can not continue.
            return true;
        }
        if(to.getId() == null) {
            // TODO: Somehow revisionable id has gone missing from TransferObject, log error
            System.err.println("Missing revisionable id on TransferObject");
            // Return false, data integrity is suspect, save can not continue.
            return true;
        }
        if(toIdField == null) {
            // TODO: saved data came without toIdField field, log error.
            // Return false, we can not be sure about incoming data integrity. Save can not continue
            System.err.println("Trying to save series without toIdField");
            return true;
        }
        if(!(to.getId().equals(toIdField))) {
            // TODO: Someone has managed to change either field value or id value for revisionable id, log error
            System.err.println("Revisionable id field and variable not equal in TransferObject");
            // Return false, data integrity suspicion
            return true;
        }
        if(!to.getId().equals(dataIdField)) {
            // TODO: data is out of sync or someone tried to change the id, log error
            // Return false since save can not continue.
            return true;
        }
        return false;
    }

    /**
     * Used for field value checks and changes automation during TransferObject saves.
     * Skips over configured idField as well as concatenate fields since both are checked separately by different
     * methods. Also skips over subfields since they are handled in their containers.
     *
     * @param key Field key of the field being checked
     * @param to TransferObject containing values from UI
     * @param time Current time in DateTime format used to set savedAt time for changes
     * @param data Current RevisionData returned from database
     * @param config Configuration mathinc the configuration key in data
     * @return True if field has changed, false if not. TODO: Return better information for error message purposes and to stop save process if there is some data tampering.
     */
    public static boolean doFieldChanges(String key, TransferObject to, LocalDateTime time, RevisionData data, Configuration config) {
        if(key.equals(config.getIdField())) {
            // Id field should have been checked separately and there should never be changes to it.
            // Skip id field.
            return false;
        }

        Field field = config.getField(key);

        if(field == null) {
            // TODO: No such field in configuration. Log error
            return false;
        } else if(field.getWritable() == false) {
            // Field is not writable, don't check for changes and don't add to RevisionData
            return false;
        } else if(field.getSubfield()) {
            // No need to process further, subfields are handled when their containers are processed
            return false;
        } else if(field.getType() == FieldType.CONCAT) {
            // Concat fields are handled after everything else is done since they require information from other fields.
            return false;
        } else if(field.getType() == FieldType.CONTAINER) {
            return doContainerChanges(key, to.getByKey(field.getKey()), time, data, config);
        } else if(field.getType() == FieldType.REFERENCECONTAINER) {
            return doReferenceContainerChanges(key, to.getByKey(field.getKey()), time, data, config);
        } else {
            return doSingleValueChanges(key, to.getByKey(key), time, data, config);
        }
    }

    /**
     * Do change checking for concat fields and calculate new values if needed.
     *
     * @param key Field key of the field being checked
     * @param to TransferObject containing values from UI
     * @param time Current time in DateTime format used to set savedAt time for changes
     * @param data Current RevisionData returned from database
     * @param config Configuration mathinc the configuration key in data
     * @return True if field has changed, false if not. TODO: Return better information for error message purposes and to stop save process if there is some data tampering.
     */
    public static boolean doConcatChanges(String key, TransferObject to, LocalDateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() != FieldType.CONCAT) {
            // This method handles only CONCAT fields. Return without changes.
            // TODO: Possibly return better information once better return object is defined.
            return false;
        }

        // TODO: Change checking, immutability checking etc.
        return false;
    }

    /**
     * Handle saving of container objects.
     * Assumes that it's used on a top level field and the provided value is the JSON send by UI.
     * Uses UI given information on what rows have been saved from dialog to detect rows for comparison
     * but only actually changed values initiate saving to RevisionData so only opening a row in UI and
     * pressing 'OK' does not mean a change is made.
     *
     * @param key Key of the top level field for this container
     * @param value Contents of the field in transfer object
     * @param time Time set to all new values
     * @param data Current revision data, changes are made to this if changes are present.
     * @param config Assumed to be the configuration for the given Revision Data.
     * @return True if there has been changes in this container, false otherwise.
     */
    // TODO: Refactor to use ModelFieldUtil and move away from JSONObject
    private static boolean doContainerChanges(String key, Object value, LocalDateTime time, RevisionData data, Configuration config) {
        // Non empty, non null String is expected, if not then can't continue
        if(!(value instanceof String) || StringUtils.isEmpty((String)value)) {
            // TODO: Possibly something wrong, log warning
            return false;
        }
        // We have a container to check
        JSONObject jsonContainer = new JSONObject((String)value);
        // More sanity checks
        if(!jsonContainer.get("type").equals("container")) {
            // TODO: Wrong data, log warning
            return false;
        }
        // There should be rows array
        JSONArray rows = jsonContainer.getJSONArray("rows");

        Field field = config.getField(key);
        if(field.getType() != FieldType.CONTAINER) {
            // If this field's type is something other than CONTAINER return false.
            // This method is meant exclusively for handling containers and nothing else.
            return false;
        }




        boolean changes = false;

        ContainerDataField container = (ContainerDataField)data.getField(field.getKey());
        if(container == null) {
            container = new ContainerDataField(field.getKey());
        }

        ContainerChange changeContainer = (ContainerChange)data.getChange(field.getKey());
        if(changeContainer == null) {
            changeContainer = new ContainerChange(field.getKey());
        }
        for(int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.getJSONObject(i);
            if(row.optBoolean("change", false) == false) {
                continue; // Row has not been opened on UI, no need to process.
            }
            if(row.opt("rowId") == null || row.opt("rowId") == JSONObject.NULL) { // If no rowId was set this is a new row. Initialise rowId for future use. This should automatically set rowId to correct value for new rows
                row.put("rowId", data.getNewRowId());
            }
            DataRow dataRow = container.getRow(stringToInteger(row.get("rowId")));
            boolean rowChanges = false;
            boolean newRow = false; // Flag for inserting the row at the end of this iteration if changes are present.

            if(dataRow == null) {
                dataRow = new DataRow(container.getKey(), stringToInteger(row.get("rowId")));
                newRow = true;
                // This should automatically lead to there being a change in one of the fields so we don't have to set
                // rowChanges to true. If for some strange reason no changes are detected then a new empty row is not
                // added to the container, which is desirable.
            }
            RowChange rowChangeContainer = changeContainer.getRows().get(dataRow.getRowId());
            if(rowChangeContainer == null) {
                rowChangeContainer = new RowChange(dataRow.getRowId());
            }
            for(String subkey : field.getSubfields()) {
                Field subfield = config.getField(subkey);
                boolean fieldChange = false;
                if(subfield.getType() == FieldType.CONCAT) {
                    // There shouldn't be concatenated fields within containers just for simplicity reasons,
                    // but if it's ever necessary this is still not the place to handle them
                    continue;
                } else if(subfield.getType() == FieldType.CONTAINER) {
                    // TODO: Handle recursive containers
                    continue;
                } else if(subfield.getType() == FieldType.REFERENCECONTAINER) {
                    // TODO: Handle recursive referencecontainer
                    continue;
                }
                if(row.opt("fields") == null || row.opt("field") == JSONObject.NULL) {
                    // TODO: if row is missing fields object it might mean row is deleted, for now continue
                    continue;
                }
                // Field is a single value field and fields exists
                JSONObject fieldValue = row.optJSONObject("fields").optJSONObject(subfield.getKey());
                SavedDataField savedField = (SavedDataField) dataRow.getField(subfield.getKey());
                Object newValue = null;
                if(fieldValue != null && fieldValue != JSONObject.NULL) {
                    if(!fieldValue.get("type").equals("value")) {
                        // TODO: For some reason value field with wrong time is present, log error.
                        continue;
                    }
                    newValue = fieldValue.opt("value");
                }
                // Check if there has been a change in field value
                fieldChange = doValueComparison(savedField, subfield, newValue);

                if(fieldChange) {
                    if(savedField == null) {
                        savedField = new SavedDataField(subfield.getKey());
                        dataRow.putField(savedField);
                    }
                    updateFieldValue(savedField, subfield, newValue, time, rowChangeContainer.getChanges());
                }
                // If this field changed or if row was changed previously then row has changed.
                rowChanges = fieldChange | rowChanges;
            } // End of field handling
            if(rowChanges) {
                dataRow.setSavedAt(time);
                //TODO: Set savedBy when information is available
                if(newRow) { // Row was created at the beginning of this iteration, add it to rows list.
                    container.putRow(dataRow);
                }
                changeContainer.getRows().put(rowChangeContainer.getRowId(), rowChangeContainer);
            }
            // If this row has changed or a row has changed previously, then this container has changed.
            changes = rowChanges | changes;
        } // End of row handling

        if(changes) {
            data.putField(container);
            data.putChange(changeContainer);
        }

        return changes;
    }

    /**
     * Handle saving of reference container objects.
     * Assumes that it's used on a top level field and the provided value is the JSON send by UI.
     * Only information saved is the actual reference. Everything else is of no interest for this purpose.
     * Uses the changed-extrafield from GUI to detect fields that need to be handled but only actually
     * saves fields that are truly changed.
     *
     * @param key Key of the top level field for this reference container
     * @param value Contents of the field in transfer object
     * @param time Time set to all new values
     * @param data Current revision data, changes are made to this if changes are present.
     * @param config Assumed to be the configuration for the given Revision Data.
     * @return True if there has been changes in this container, false otherwise.
     */
    private static boolean doReferenceContainerChanges(String key, Object value, LocalDateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() != FieldType.REFERENCECONTAINER) {
            // If this field's type is something other than REFERENCECONTAINER return false.
            // This method is meant exclusively for handling reference containers and nothing else.
            return false;
        }
        // Non empty, non null String is expected, if not then can't continue
        if(!(value instanceof String) || StringUtils.isEmpty((String)value)) {
            // TODO: Possibly something wrong, log warning
            return false;
        }
        JSONObject json = new JSONObject((String)value);

        // More sanity checks
        if(!json.get("type").equals("referencecontainer")) {
            // TODO: Wrong data, log warning
            return false;
        }
        boolean changes = false;
        ReferenceContainerDataField referenceContainer = (ReferenceContainerDataField)data.getField(field.getKey());
        if(referenceContainer == null) {
            referenceContainer = new ReferenceContainerDataField(field.getKey());
        }
        JSONArray references = json.getJSONArray("references");
        ContainerChange changeContainer = (ContainerChange)data.getChange(field.getKey());
        if(changeContainer == null) {
            changeContainer = new ContainerChange(field.getKey());
        }
        for(int i = 0; i < references.length(); i++) {
            JSONObject reference = references.getJSONObject(i);
            if(reference.optBoolean("change", false) == false) {
                continue; // Row has not been edited on UI, no need to process.
            }
            if(reference.opt("rowId") == null || reference.opt("rowId") == JSONObject.NULL) { // If no rowId was set this is a new reference. Initialise rowId for future use. This should automatically set rowId to correct value for new references
                reference.put("rowId", data.getNewRowId());
            }
            SavedReference savedReference = referenceContainer.getReference(stringToInteger(reference.get("rowId")));
            boolean referenceChanges = false;
            boolean newReference = false; // Flag for inserting the reference at the end of this iteration if changes are present.

            if(savedReference == null) {
                savedReference = new SavedReference(referenceContainer.getKey(), stringToInteger(reference.get("rowId")));
                newReference = true;
                // This should automatically lead to there being a change in one of the fields so we don't have to set
                // referenceChanges to true. If for some strange reason no changes are detected then a new empty row is not
                // added to the container, which is desirable.
            }
            if(reference.optString("value", null) == null && savedReference.getValue() == null) {
                // Both new value and old value are empty, no need for changes.
                continue;
            } else if(reference.optString("value", null) != null) {
                // There is a value, check to see if it's the same as old value
                if(savedReference.hasValue() && savedReference.valueEquals(reference.get("value").toString())) {
                    // Old value and new value are same, no changes
                    continue;
                } else {
                    // Some kind of change, stick new value as modified value to saved reference
                    SavedValue savedValue = new SavedValue();
                    savedValue.setSavedAt(time);
                    //TODO: Set savedBy when information is available
                    setSimpleValue(savedValue, reference.get("value").toString());
                    savedReference.setModifiedValue(savedValue);
                    referenceChanges = true;
                }
            }

            if(referenceChanges) {
                if(newReference) { // Row was created at the beginning of this iteration, add it to rows list.
                    referenceContainer.getReferences().add(savedReference);
                }
                changeContainer.getRows().put(savedReference.getRowId(), new RowChange(savedReference.getRowId()));
            }
            // If this reference has changed or a reference has changed previously, then this container has changed.
            changes = referenceChanges | changes;
        } // End of row handling

        if(changes) {
            data.putField(referenceContainer);
            data.putChange(changeContainer);
        }

        return changes;
    }

    /**
     * Handle change checking for top level field.
     * This should only be used for top level (not subfield to anything) non concat and non container fields since containers,
     * rows and subfields are handled differently.
     *
     * @param key Field key of the value being checked.
     * @param value Value being checked against. Since we check that given key does not refer to a container we can assume that value is a field value.
     * @param data Original data in the database
     * @param config configuration of the data being validated
     * @return Boolean telling has there been a change in the given value or no
     */
    private static boolean doSingleValueChanges(String key, Object value, LocalDateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() == FieldType.CONCAT || field.getType() == FieldType.CONTAINER || field.getType() == FieldType.REFERENCECONTAINER || field.getSubfield()) {
            // If this field is of type CONCAT or CONTAINER or a subfield to something then changes can't be handled here.
            // Container fields are handled separately and have a whole process for each row and subfield.
            // Concat values should be checked at the end just by concatenating the values again and seeing if it's a new value.
            // Subfield changes are not recorded as separate changes but the whole row is marked changed if even one field has changed.
            return false;
        }

        Pair<StatusCode, SavedDataField> pair = data.dataField(SavedDataFieldCall.get(key).setConfiguration(config));
        SavedDataField container = pair.getRight();

        // Do checking for change
        boolean change = doValueComparison(container, field, value);

        // If change has happened insert new change to data.
        // This method handles only top level fields so change can be inserted directly to data.
        if(change) {
            if(container == null) {
                container = new SavedDataField(field.getKey());
                data.putField(container);
            }
            updateFieldValue(container, field, value, time, data.getChanges());
        }
        return change;
    }

    /**
     * Checks given SavedDataField against given value using given Field configuration.
     *
     * @param container Current value to be compared to
     * @param field Configuration for current value
     * @param value New value to be compared
     * @return True if change is detected, false otherwise
     */
    private static boolean doValueComparison(SavedDataField container, Field field, Object value) {
        // Sanity check, if configuration is for wrong container then don't compare
        if(container != null && !container.getKey().equals(field.getKey())) {
            return false; // Tried to compare field with wrong configuration.
        }
        String oldValue = extractStringSimpleValue(container);
        oldValue = (StringUtils.isEmpty(oldValue)) ? null : oldValue;
        boolean change = false;
        switch(field.getType()) {
            case DATE: // TODO: Actual DATE, DATETIME and TIME handling. For now treat as string
            case DATETIME:
            case TIME:
            case REFERENCE: // For now treat as a simple value and assume that the value comes from a valid source.
            case SELECTION: // Selection can be treated as a string since only selected value is saved and it is saved as a string.
                // TODO: Separate saving of SELECTION to enable saving freeText value
            case STRING: { // Assume correctness and treat values as string.

                /*String strValue = "";
                try{
                strValue = (String)value;
                }catch(ClassCastException ex) {
                    int temp = 0;
                }*/
                String strValue = (String)value;
                strValue = (StringUtils.isEmpty(strValue)) ? null : strValue;

                if(strValue == null) { // new value is empty (this checks for empty string as well as null)
                    change = !(oldValue == null); // If old value is empty then no changes has happened
                } else { // new value is not empty
                    change = !(strValue.equals(oldValue)); // If the old and new values are equal then no change has happened.
                }

                break;
            }
            // TODO: Either combine INTEGER and STRING setting or then make different values that know how to handle their datatypes
            case INTEGER: { // Assume correctness and treat value as number
                Long intValue = stringToLong(value);
                if(intValue == null) { // New value is null, change depends on the old value existing.
                    change = !(oldValue == null); // If old value is null, no change, otherwise change.
                } else {
                    change = !(intValue.equals(stringToLong(oldValue))); // Check equality to old value, assume integer at this point.
                }

                break;
            }
            // TODO: Add comparisons for rest of the types.
            default:
                // TODO: Unhandled type, log error
                change = false;
                break;
        }
        // Check for change in immutable field. If change has been detected, field is immutable and oldValue is not null then reverse change.
        if(field.getImmutable() && change) {
            if(oldValue != null) {
                // TODO: Return some sort of notification that non empty immutable value was about to change. For now reverse change.
                change = false;
            }
        }
        return change;
    }

    /**
     * Initialises new revision from old revision data
     * @param oldData Source revision
     * @param newData Target revision
     */
    public static void copyFieldsToNewRevision(RevisionData oldData, RevisionData newData) {
        for(DataField field : oldData.getFields().values()) {
            newData.putField(field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }
    }
}
