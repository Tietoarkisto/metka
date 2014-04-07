package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

public class ModelAccessUtil {
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
        SavedDataField field = getSavedDataFieldFromRevisionData(data, key, config);
        if(field == null) {
            // TODO: tried to check id integrity with Container field. Container field should never be id field. Log error
            // Tried to check integrity with wrong field, integrity can not be checked.
            return false;
        }
        Integer dataIdField = extractIntegerSimpleValue(field);
        Integer toIdField = stringToInteger(to.getByKey(key));
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
     * Return top level SavedDataField for given field key.
     * If field does not exist in configuration returns null
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is a CONTAINER field return null.
     *
     * Otherwise returns field from data typed as SavedDataField (assumes data is correct)
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return SavedDataField of the requested key if one exists.
     */
    public static SavedDataField getSavedDataFieldFromRevisionData(RevisionData data, String key, Configuration config) {
        if(config.getField(key) == null) {
            // TODO: no such field in configuration, log error.
            // Field does not exist. return null
            return null;
        }
        if(config.getField(key).getSubfield()) {
            // TODO: Tried to request a CONTAINER that is a subfield, log error.
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getType() == FieldType.CONTAINER) {
            // TODO: Tried to get ContainerDataField instead of SavedDataField, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getType() == FieldType.REFERENCECONTAINER) {
            // TODO: Tried to get ReferenceContainerDataField instead of SavedDataField, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }

        return (SavedDataField)data.getField(key);
    }

    /**
     * Return top level SavedDataField for given field key.
     * Contains less sanity checks since no configuration is provided.
     *
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @return SavedDataField of the requested key if one exists.
     */
    public static SavedDataField getSavedDataFieldFromRevisionData(RevisionData data, String key) {
        DataField field = data.getField(key);
        if(field instanceof SavedDataField) {
            return (SavedDataField)field;
        } else {
            // Field isn't a requested type of field, return null
            return null;
        }
    }

    /**
     * Return top level ContainerDataField for given field key.
     * If field does not exist in configuration returns null.
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is not a CONTAINER field return null.
     *
     * Otherwise returns field from data typed as ContainerDataField (assumes data is correct).
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return ContainerDataField of the requested key if one exists.
     */
    public static ContainerDataField getContainerDataFieldFromRevisionData(RevisionData data, String key, Configuration config) {
        if(config.getField(key) == null) {
            // TODO: no such field in configuration, log error.
            // Field does not exist. return null
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getSubfield()) {
            // TODO: Tried to request a CONTAINER that is a subfield, log error.
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getType() != FieldType.CONTAINER) {
            // TODO: Tried to get field that is not a Container, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }

        return (ContainerDataField)data.getField(key);
    }

    /**
     * Return top level ReferenceContainerDataField for given field key.
     * If field does not exist in configuration returns null.
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is not a REFERENCECONTAINER field return null.
     *
     * Otherwise returns field from data typed as ContainerDataField (assumes data is correct).
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return ReferenceContainerDataField of the requested key if one exists.
     */
    public static ReferenceContainerDataField getReferenceContainerDataFieldFromRevisionData(RevisionData data, String key, Configuration config) {
        if(config.getField(key) == null) {
            // TODO: no such field in configuration, log error.
            // Field does not exist. return null
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getSubfield()) {
            // TODO: Tried to request a CONTAINER that is a subfield, log error.
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }
        if(config.getField(key).getType() != FieldType.REFERENCECONTAINER) {
            // TODO: Tried to get field that is not a ReferenceContainer, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }

        return (ReferenceContainerDataField)data.getField(key);
    }

    /**
     * Initialises and returns a SavedDataField that is usable in a new revision.
     * Performs a sanity check to see if given field should be initialised for new revision at all.
     * Returns null if given field should not be inserted into new revision.
     * @param field Old SavedDataField that is transformed to new revision
     * @return SavedDataField that can be inserted into a new revision or null.
     */
    public static SavedDataField createNewRevisionSavedDataField(SavedDataField field) {
        SavedDataField newField = null;
        if(field != null && field.getValue() != null) { // sanity check
            newField = new SavedDataField(field.getKey());
            newField.setOriginalValue(field.getValue());
        }
        return newField;
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
    private static boolean doContainerChanges(String key, Object value, LocalDateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() != FieldType.CONTAINER) {
            // If this field's type is something other than CONTAINER return false.
            // This method is meant exclusively for handling containers and nothing else.
            return false;
        }
        // Non empty, non null String is expected, if not then can't continue
        if(!(value instanceof String) || StringUtils.isEmpty((String)value)) {
            // TODO: Possibly something wrong, log warning
            return false;
        }
        JSONObject jsonContainer = new JSONObject((String)value);

        // More sanity checks
        if(!jsonContainer.get("type").equals("container")) {
            // TODO: Wrong data, log warning
            return false;
        }
        boolean changes = false;
        ContainerDataField container = (ContainerDataField)data.getField(field.getKey());
        if(container == null) {
            container = new ContainerDataField(field.getKey());
        }
        JSONArray rows = jsonContainer.getJSONArray("rows");
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
                    updateFieldValue(savedField, subfield, newValue, time);
                    rowChangeContainer.putChange(new Change(subkey));
                }
                // If this field changed or if row was changed previously then row has changed.
                rowChanges = fieldChange | rowChanges;
            } // End of field handling
            if(rowChanges) {
                dataRow.setSavedAt(time);
                //TODO: Set savedBy when information is available
                if(newRow) { // Row was created at the beginning of this iteration, add it to rows list.
                    container.getRows().add(dataRow);
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
                if(savedReference.hasValue() && savedReference.valueEquals(reference.getString("value"))) {
                    // Old value and new value are same, no changes
                    continue;
                } else {
                    // Some kind of change, stick new value as modified value to saved reference
                    SavedValue savedValue = new SavedValue();
                    savedValue.setSavedAt(time);
                    //TODO: Set savedBy when information is available
                    setSimpleValue(savedValue, reference.getString("value"));
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

        SavedDataField container = getSavedDataFieldFromRevisionData(data, key, config);

        // Do checking for change
        boolean change = doValueComparison(container, field, value);

        // If change has happened insert new change to data.
        // This method handles only top level fields so change can be inserted directly to data.
        if(change) {
            if(container == null) {
                container = new SavedDataField(field.getKey());
                data.putField(container);
            }
            updateFieldValue(container, field, value, time);
            data.putChange(new Change(field.getKey()));
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
            case CHOICE: // Choice can be treated as a string since only choice value is saved and it is saved as a string.
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
            case INTEGER: { // Assume correctness and treat value as Integer
                Integer intValue = stringToInteger(value);
                if(intValue == null) { // New value is null, change depends on the old value existing.
                    change = !(oldValue == null); // If old value is null, no change, otherwise change.
                } else {
                    change = !(intValue.equals(stringToInteger(oldValue))); // Check equality to old value, assume integer at this point.
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
     * Updates the value in given Container using typed procedures
     * @param container SavedDataField to be modified
     * @param field Configuration for given container
     * @param value New value to be inserted
     * @param time Requested time for modification. If not present then current time is used
     */
    private static void updateFieldValue(SavedDataField container, Field field, Object value, LocalDateTime time) {
        // Sanity check to see that container exists and configuration is for right container
        if(container == null || !container.getKey().equals(field.getKey())) {
            return;
        }
        if(time == null) {
            time = new LocalDateTime();
        }
        switch (field.getType()) {
            case INTEGER:
                Integer intValue = stringToInteger(value);
                insertIntegerValueChange(time, intValue, container);
                break;
            default:
                insertStringValueChange(time, (String)value, container);
                break;
        }
    }

    /**
     * Default insertion method for modifying SavedDataField.
     * In theory can be used for everything but some types might need more sophisticated methods or conversions.
     * @param time Requested time for the change
     * @param value New value to be inserted
     * @param container SavedDataField to be modified.
     */
    private static void insertStringValueChange(LocalDateTime time, String value, SavedDataField container) {
        SavedValue saved = createSavedValue(time);
        if(!StringUtils.isEmpty(value)) {
            setSimpleValue(saved, value);
        }
        updateSavedDataField(container, saved);
    }

    /**
     * For now this is a redundant implementation of insert value change but it is here because other types might need
     * different implementations and so they need different methods. Because of this there might as well be one method
     * per type.
     * @param time DateTime of change
     * @param value New value
     * @param container SavedField where the new value is to be inserted
     */
    private static void insertIntegerValueChange(LocalDateTime time, Integer value, SavedDataField container) {
        SavedValue saved = createSavedValue(time);
        if(value != null) {
            setSimpleValue(saved, value.toString());
        }
        updateSavedDataField(container, saved);
    }

    /**
     * Actually inserts the given SavedValue into a SavedDataField.
     * Performs a sanity check to see that null values are not inserted.
     * @param container
     * @param saved
     */
    private static void updateSavedDataField(SavedDataField container, SavedValue saved) {
        // TODO: Improve by taking out unnecessary cases such as no original value and new SavedValue has no value.

        if(saved != null) {
            container.setModifiedValue(saved);
        }
    }

    /**
     * If there is a value then assume SimpleValue and return it as an Integer.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedDataField to get value.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned through ConversionUtil.stringToInteger conversion.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed integer.
     *          This can return null.
     * @param field SavedDataField from where the value is extracted from.
     * @return Integer gained by using ConversionUtil.stringToInteger on the simple value representation if value is found.
     */
    public static Integer extractIntegerSimpleValue(SavedDataField field) {
        Integer integer = null;
        if(field != null && field.hasValue()) {
            integer = stringToInteger(field.getActualValue());
        }

        return integer;
    }

    /**
     * If there is a value then assume SimpleValue and return it as a String.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedDataField to get SavedValue.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned.
     * NOTICE:  No matter what the configuration for the field says this method returns Strings.
     * @param field Container from where the value is extracted from.
     * @return String containing the simple value representation if value is found.
     */
    public static String extractStringSimpleValue(SavedDataField field) {
        String string = null;
        if(field != null && field.hasValue()) {
            string = field.getActualValue();
        }

        return string;
    }

    /*
    * This clears existing value from given SavedValue and inserts a new SimpleValue with the given
    * value.
    *
    * WARNING: This does not in any way check what type of value this field is supposed to have
     *         so you can easily insert illegal values with this.
     */
    public static SavedValue setSimpleValue(SavedValue saved, String value) {
        saved.setValue(new SimpleValue(value));
        return saved;
    }

    /**
     * Creates and initialises a new SavedValue from given parameters.
     * @param time Time when this container was requested.
     * @return Initialised SavedValue ready for use
     */
    public static SavedValue createSavedValue(LocalDateTime time) {
        SavedValue value = new SavedValue();
        value.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return value;
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
