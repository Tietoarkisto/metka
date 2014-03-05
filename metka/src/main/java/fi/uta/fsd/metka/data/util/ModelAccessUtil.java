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
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

public class ModelAccessUtil {

    /**
     * Returns a FieldContainer using field path navigation.
     * If supplied path doesn't lead to a legal FieldContainer then null is returned.
     * Since both Containers and Rows are also FieldContainers both can be returned with this method.
     *
     * Field path consist of one or more steps separated by period.
     * Each step is either a field key that should be found from given position or an integer denoting a rowId for
     * specific row.
     * @param path Path to requested FieldContainer
     * @param data RevisionData from which the FieldContainer should be returned.
     * @return FieldContainer found from the specific path.
     */
    public static FieldContainer getFieldDotFormat(String path, RevisionData data) {
        List<String> steps = Arrays.asList(path.split("."));
        FieldContainer field = null;
        for(String step : steps) {
            if(field == null) {
                field = data.getField(step);
                if(field == null) {
                    // Path terminates here, no starting field found. Return null.
                    return null;
                }
            } else {
                if(field instanceof ContainerFieldContainer) {
                    ContainerFieldContainer container = (ContainerFieldContainer)field;
                    Integer rowId = stringToInteger(step);
                    if(rowId != null) {
                        field = null;
                        for(RowContainer row : container.getRows()) {
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
                } else if(field instanceof RowContainer) {
                    RowContainer row = (RowContainer)field;
                    field = row.getField(step);
                    if(field == null) {
                        // Path terminates here, no matter if there are more steps. Return null.
                        return null;
                    }
                }
            }
        }
        return field;
    }

    public static boolean idIntegrityCheck(TransferObject to, RevisionData data, Configuration config) {
        String key = config.getIdField();
        SavedFieldContainer field = getSavedFieldContainerFromRevisionData(data, key, config);
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
     * Return top level SavedFieldContainer for given field key.
     * If field does not exist in configuration returns null
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is a CONTAINER field return null.
     *
     * Otherwise returns field from data typed as SavedFieldContainer (assumes data is correct)
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return SavedFieldContainer of the requested key if one exists.
     */
    public static SavedFieldContainer getSavedFieldContainerFromRevisionData(RevisionData data, String key, Configuration config) {
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
            // TODO: Tried to get ContainerFieldContainer instead of ValueFieldContainer, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }

        return (SavedFieldContainer)data.getField(key);
    }

    /**
     * Return top level SavedFieldContainer for given field key.
     * Contains less sanity checks since no configuration is provided.
     *
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @return SavedFieldContainer of the requested key if one exists.
     */
    public static SavedFieldContainer getSavedFieldContainerFromRevisionData(RevisionData data, String key) {
        FieldContainer field = data.getField(key);
        if(field instanceof SavedFieldContainer) {
            return (SavedFieldContainer)field;
        } else {
            // Field isn't a requested type of field, return null
            return null;
        }
    }

    /**
     * Return top level ContainerFieldContainer for given field key.
     * If field does not exist in configuration returns null.
     * If field is a subfield return null, this should only be used for top level (non subfield) containers.
     * If field is not a CONTAINER field return null.
     *
     * Otherwise returns field from data typed as ContainerFieldContainer (assumes data is correct).
     * @param data RevisionData of the revision being manipulated.
     * @param key Field key of the requested field.
     * @param config Configuration of the provided RevisionData
     * @return ContainerFieldContainer of the requested key if one exists.
     */
    public static ContainerFieldContainer getContainerFieldContainerFromRevisionData(RevisionData data, String key, Configuration config) {
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
            // TODO: Tried to get ValueFieldContainer instead of ContainerFieldContainer, log error
            // TODO: Should return an error message or some sort of result object to inform that illegal operation has happened
            return null;
        }

        return (ContainerFieldContainer)data.getField(key);
    }

    /**
     * Initialises and returns a SavedFieldContainer that is usable in a new revision.
     * Performs a sanity check to see if given field should be initialised for new revision at all.
     * Returns null if given field should not be inserted into new revision.
     * @param field Old SavedFieldContainer that is transformed to new revision
     * @return SavedFieldContainer that can be inserted into a new revision or null.
     */
    public static SavedFieldContainer createNewRevisionSavedFieldContainer(SavedFieldContainer field) {
        SavedFieldContainer newField = null;
        if(field != null && field.getValue() != null) { // sanity check
            newField = new SavedFieldContainer(field.getKey());
            newField.setOriginalValue(field.getValue());
        }
        return newField;
    }


    /*
     * When you create a new revision you need to copy old field values as changes to the new DRAFT.
     * This method does the initialisation of the Change object for you.
     * This method is only for ValueFieldChanges, ContainerFieldChanges need their own.
     * @param field the actual value container for the field.
     * @return

    public static ValueFieldChange createNewRevisionValueFieldChange(ValueFieldContainer field) {
        ValueFieldChange change = new ValueFieldChange(field.getKey());
        change.setOperation(ChangeOperation.UNCHANGED);
        change.setOriginalField(field);

        return change;
    }*/

    /*
     * Creates and initialises a new ValueFieldContainer from given parameters.
     * @param key Field key of the needed container
     * @param time Time when this container was requested.
     * @return Initialised ValueFieldContainer ready for use

    public static ValueFieldContainer createValueFieldContainer(String key, DateTime time) {
        ValueFieldContainer field = new ValueFieldContainer(key);
        field.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return field;
    }*/

    /**
     * Used for field value checks and changes automation during TransferObject saves.
     * Skips over configured idField as well as concatenate fields since both are checked separately by different
     * methods.
     *
     * @param key Field key of the field being checked
     * @param to TransferObject containing values from UI
     * @param time Current time in DateTime format used to set savedAt time for changes
     * @param data Current RevisionData returned from database
     * @param config Configuration mathinc the configuration key in data
     * @return True if field has changed, false if not. TODO: Return better information for error message purposes and to stop save process if there is some data tampering.
     */
    public static boolean doFieldChanges(String key, TransferObject to, DateTime time, RevisionData data, Configuration config) {
        if(key.equals(config.getIdField())) {
            // Id field should have been checked separately and there should never be changes to it.
            // Skip id field.
            return false;
        }

        Field field = config.getField(key);
        if(field == null) {
            // TODO: No such field in configuration. Log error
            return false;
        } else if(field.getType() == FieldType.CONCAT) {
            // Concat fields are handled after everything else is done since they require information from other fields.
            return false;
        } else if(field.getType() == FieldType.CONTAINER) {
            return doContainerChanges(key, to.getByKey(field.getKey()), time, data, config);
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
    public static boolean doConcatChanges(String key, TransferObject to, DateTime time, RevisionData data, Configuration config) {
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
    private static boolean doContainerChanges(String key, Object value, DateTime time, RevisionData data, Configuration config) {
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
        ContainerFieldContainer container = (ContainerFieldContainer)data.getField(field.getKey());
        if(container == null) {
            container = new ContainerFieldContainer(field.getKey());
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
                row.put("rowId", container.getNextRowId());
                container.setNextRowId(container.getNextRowId()+1);
            }
            RowContainer rowContainer = container.getRow(stringToInteger(row.get("rowId")));
            boolean rowChanges = false;
            boolean newRow = false; // Flag for inserting the row at the end of this iteration if changes are present.

            if(rowContainer == null) {
                rowContainer = new RowContainer(container.getKey(), stringToInteger(row.get("rowId")));
                newRow = true;
                // This should automatically lead to there being a change in one of the fields so we don't have to set
                // rowChanges to true. If for some strange reason no changes are detected then a new empty row is not
                // added to the container, which is desirable.
            }
            RowChange rowChangeContainer = changeContainer.getRows().get(rowContainer.getRowId());
            if(rowChangeContainer == null) {
                rowChangeContainer = new RowChange(rowContainer.getKey(), rowContainer.getRowId());
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
                }
                // Field is a single value field.
                JSONObject fieldValue = row.getJSONObject("fields").getJSONObject(subfield.getKey());
                SavedFieldContainer savedField = (SavedFieldContainer)rowContainer.getField(subfield.getKey());

                // Check if there has been a change in field value
                fieldChange = doValueComparison(savedField, subfield, fieldValue.get("value"));

                if(fieldChange) {
                    if(savedField == null) {
                        savedField = new SavedFieldContainer(subfield.getKey());
                        rowContainer.putField(savedField);
                    }
                    updateFieldValue(savedField, subfield, fieldValue.get("value"), time);
                    rowChangeContainer.putChange(new Change(field.getKey()));
                }
                // If this field changed or if row was changed previously then row has changed.
                rowChanges = fieldChange | rowChanges;
            } // End of field handling
            if(rowChanges) {
                rowContainer.setSavedAt(time);
                //TODO: Set savedBy when information is available
                if(newRow) { // Row was created at the beginning of this iteration, add it to rows list.
                    container.getRows().add(rowContainer);
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
    private static boolean doSingleValueChanges(String key, Object value, DateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() == FieldType.CONCAT || field.getType() == FieldType.CONTAINER || field.getSubfield()) {
            // If this field is of type CONCAT or CONTAINER or a subfield to something then changes can't be handled here.
            // Container fields are handled separately and have a whole process for each row and subfield.
            // Concat values should be checked at the end just by concatenating the values again and seeing if it's a new value.
            // Subfield changes are not recorded as separate changes but the whole row is marked changed if even one field has changed.
            return false;
        }

        SavedFieldContainer container = getSavedFieldContainerFromRevisionData(data, key, config);

        // Do checking for change
        boolean change = doValueComparison(container, field, value);

        // If change has happened insert new change to data.
        // This method handles only top level fields so change can be inserted directly to data.
        if(change) {
            if(container == null) {
                container = new SavedFieldContainer(field.getKey());
                data.putField(container);
            }
            updateFieldValue(container, field, value, time);
            data.putChange(new Change(field.getKey()));
        }
        return change;
    }

    /**
     * Checks given SavedFieldContainer against given value using given Field configuration.
     *
     * @param container Current value to be compared to
     * @param field Configuration for current value
     * @param value New value to be compared
     * @return True if change is detected, false otherwise
     */
    private static boolean doValueComparison(SavedFieldContainer container, Field field, Object value) {
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
            case DERIVED: // We are only interested in the key part of the derived value. Actual derived text is added during approval.
            case CHOICE: // Choice can be treated as a string since only choice value is saved and it is saved as a string.
            case STRING: { // Assume correctness and treat values as string.
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
     * @param container SavedFieldContainer to be modified
     * @param field Configuration for given container
     * @param value New value to be inserted
     * @param time Requested time for modification. If not present then current time is used
     */
    private static void updateFieldValue(SavedFieldContainer container, Field field, Object value, DateTime time) {
        // Sanity check to see that container exists and configuration is for right container
        if(container == null || !container.getKey().equals(field.getKey())) {
            return;
        }
        if(time == null) {
            time = new DateTime();
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
     * Default insertion method for modifying SavedFieldContainer.
     * In theory can be used for everything but some types might need more sophisticated methods or conversions.
     * @param time Requested time for the change
     * @param value New value to be inserted
     * @param container SavedFieldContainer to be modified.
     */
    private static void insertStringValueChange(DateTime time, String value, SavedFieldContainer container) {
        SavedValue saved = createSavedValue(time);
        if(!StringUtils.isEmpty(value)) {
            setSimpleValue(saved, value);
        }
        updateSavedFieldContainer(container, saved);
    }

    /**
     * For now this is a redundant implementation of insert value change but it is here because other types might need
     * different implementations and so they need different methods. Because of this there might as well be one method
     * per type.
     * @param time DateTime of change
     * @param value New value
     * @param container SavedField where the new value is to be inserted
     */
    private static void insertIntegerValueChange(DateTime time, Integer value, SavedFieldContainer container) {
        SavedValue saved = createSavedValue(time);
        if(value != null) {
            setSimpleValue(saved, value.toString());
        }
        updateSavedFieldContainer(container, saved);
    }

    /**
     * Actually inserts the given SavedValue into a SavedFieldContainer.
     * Performs a sanity check to see that null values are not inserted.
     * @param container
     * @param saved
     */
    private static void updateSavedFieldContainer(SavedFieldContainer container, SavedValue saved) {
        // TODO: Improve by taking out unnecessary cases such as no original value and new SavedValue has no value.

        if(saved != null) {
            container.setModifiedValue(saved);
        }
    }

    /**
     * If there is a value then assume SimpleValue and return it as an Integer.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedFieldContainer to get value.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned through ConversionUtil.stringToInteger conversion.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed integer.
     *          This can return null.
     * @param field SavedFieldContainer from where the value is extracted from.
     * @return Integer gained by using ConversionUtil.stringToInteger on the simple value representation if value is found.
     */
    public static Integer extractIntegerSimpleValue(SavedFieldContainer field) {
        Integer integer = null;
        if(field != null && field.getValue() != null && field.getValue().getValue() != null) {
            String value = ((SimpleValue)(field.getValue().getValue())).getValue();
            integer = stringToInteger(value);
        }

        return integer;
    }

    /**
     * If there is a value then assume SimpleValue and return it as a String.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedFieldContainer to get SavedValue.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned.
     * NOTICE:  No matter what the configuration for the field says this method returns Strings.
     * @param field Container from where the value is extracted from.
     * @return String containing the simple value representation if value is found.
     */
    public static String extractStringSimpleValue(SavedFieldContainer field) {
        String string = null;
        if(field != null && field.getValue() != null && field.getValue().getValue() != null) {
            string = ((SimpleValue)(field.getValue().getValue())).getValue();
        }

        return string;
    }

    /*
    * This clears existing values from given fields and inserts a new SimpleValue with the given
    * value.
    * This is only for ValueFieldContainers, ContainerFieldContainers as well as RowContainers work
    * on different basis altogether.
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
     * @return Initialised ValueFieldContainer ready for use
     */
    public static SavedValue createSavedValue(DateTime time) {
        SavedValue value = new SavedValue();
        value.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return value;
    }
}
