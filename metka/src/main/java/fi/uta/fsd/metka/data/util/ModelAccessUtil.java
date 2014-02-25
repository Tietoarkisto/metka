package fi.uta.fsd.metka.data.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.mvc.domain.simple.TransferObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

public class ModelAccessUtil {

    @Autowired
    private ObjectMapper metkaObjectMapper;

    public static boolean idIntegrityCheck(TransferObject to, RevisionData data, Configuration config) {
        String key = config.getIdField();
        ValueFieldContainer field = getValueFieldContainerFromRevisionData(data, key);
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

    // TODO: Configuration checking that the field is a valid target, for now assume accurate use
    public static ValueFieldContainer getValueFieldContainerFromRevisionData(RevisionData data, String key) {
        ValueFieldContainer container = null;
        if(data.getState() == RevisionState.DRAFT) {
            ValueFieldChange change = (ValueFieldChange)data.getChange(key);
            if(change == null) {
                container = (ValueFieldContainer)data.getField(key);
            } else if(change.getNewField() != null) {
                container = change.getNewField();
            } else {
                container = change.getOriginalField();
            }
        } else {
            container = (ValueFieldContainer)data.getField(key);
        }

        return container;
    }

    /**
     * If there is a value then assume SimpleValue and return it as an Integer.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned through ConversionUtil.stringToInteger conversion.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed integer.
     *          This can return null.
     * @param field Container from where the value is extracted from.
     * @return Integer gained by using ConversionUtil.stringToInteger on the simple value representation if value is found.
     */
    public static Integer extractIntegerSimpleValue(ValueFieldContainer field) {
        Integer integer = null;
        if(field != null && field.getValue() != null) {
            String value = ((SimpleValue) field.getValue()).getValue();
            integer = stringToInteger(value);
        }

        return integer;
    }

    /**
     * If there is a value then assume SimpleValue and return it as a String.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned.
     * NOTICE:  No matter what the configuration for the field says this method returns Strings.
     * @param field Container from where the value is extracted from.
     * @return String containing the simple value representation if value is found.
     */
    public static String extractStringSimpleValue(ValueFieldContainer field) {
        String string = null;
        if(field != null && field.getValue() != null) {
            string = ((SimpleValue) field.getValue()).getValue();
        }

        return string;
    }

    /**
     * When you create a new revision you need to copy old field values as changes to the new DRAFT.
     * This method does the initialisation of the Change object for you.
     * This method is only for ValueFieldChanges, ContainerFieldChanges need their own.
     * @param field the actual value container for the field.
     * @return
     */
    public static ValueFieldChange createNewRevisionValueFieldChange(ValueFieldContainer field) {
        ValueFieldChange change = new ValueFieldChange(field.getKey());
        change.setOperation(ChangeOperation.UNCHANGED);
        change.setOriginalField(field);

        return change;
    }

    /*
    * This clears existing values from given fields and inserts a new SimpleValue with the given
    * value.
    * This is only for ValueFieldContainers, SubfieldContainers need their own and ContainerFieldContainers
    * as well as RowContainers work on different basis altogether.
    * WARNING: This does not in any way check what type of value this field is supposed to have
     *         so you can easily insert illegal values with this.
     */
    public static void setSimpleValue(ValueFieldContainer field, String value) {
        field.setValue(new SimpleValue(value));
    }

    /**
     * Creates and initialises a new ValueFieldContainer from given parameters.
     * @param key Field key of the needed container
     * @param time Time when this container was requested.
     * @return Initialised ValueFieldContainer ready for use
     */
    public static ValueFieldContainer createValueFieldContainer(String key, DateTime time) {
        ValueFieldContainer field = new ValueFieldContainer(key);
        field.setSavedAt(time);
        // TODO: set current user that requests this new field container

        return field;
    }

    // TODO: Configuration checking that the field is a valid target, for now assume accurate use
    public static ValueFieldChange updateValueField(RevisionData data, String key, ValueFieldContainer newValue) {
        ValueFieldChange change = (ValueFieldChange)data.getChange(key);
        if(change == null) {
            change = new ValueFieldChange(key);
        }
        if(newValue.getValue() == null) {
            change.setOperation(ChangeOperation.REMOVED);
        } else {
            change.setOperation(ChangeOperation.MODIFIED);
        }
        change.setNewField(newValue);
        return change;
    }

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
        if(field.getType() == FieldType.CONCAT) {
            // Concat fields are handled after everything else is done since they require information from other fields.
            return false;
        } else if(field.getType() == FieldType.CONTAINER) {
            return doContainerChanges(key, to, time, data, config);
        } else {
            return doSingleValueChanges(key, to, time, data, config);
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
     * Checks to see if new value is different from old value.
     * This should only be used for top level (not subfield to anything) non concat and non container fields since containers,
     * rows and subfields are handled differently.
     *
     * @param key Field key of the value being checked.
     * @param to TransferObject currently being checked. Since we can use the general getByKey method we need only SimpleObject reference.
     * @param data Original data in the database
     * @param config configuration of the data being validated
     * @return Boolean telling has there been a change in the given value or no
     */
    private static boolean doSingleValueChanges(String key, TransferObject to, DateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() == FieldType.CONCAT || field.getType() == FieldType.CONTAINER || field.getSubfield()) {
            // If this field is of type CONCAT or CONTAINER or a subfield to something then changes can't be handled here.
            // Container fields are handled separately and have a whole process for each row and subfield.
            // Concat values should be checked at the end just by concatenating the values again and seeing if it's a new value.
            // Subfield changes are not recorded as separate changes but the whole row is marked changed if even one field has changed.
            return false;
        }
        Object value = to.getByKey(key);
        ValueFieldContainer container = getValueFieldContainerFromRevisionData(data, key);

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
                String oldValue = extractStringSimpleValue(container);
                oldValue = (StringUtils.isEmpty(oldValue)) ? null : oldValue;
                if(strValue == null) { // new value is empty (this checks for empty string as well as null)
                    change = !(oldValue == null); // If old value is empty then no changes has happened
                } else { // new value is not empty
                    change = !(strValue.equals(oldValue)); // If the old and new values are equal then no change has happened.
                }
                if(field.getImmutable() && change) {
                    if(oldValue != null) {
                        // TODO: Return some sort of notification that non empty immutable value was about to change. For now reverse change.
                        change = false;
                    }
                }
                if(change) {
                    insertStringValueChange(key, time, strValue, data);
                }

                return change;
            }
            case INTEGER: { // Assume correctness and treat value as Integer
                Integer intValue = stringToInteger(value);
                Integer oldValue = extractIntegerSimpleValue(container); // Get old value as Integer, can't be null since null values are not saved.
                if(intValue == null) { // New value is null, change depends on the old value existing.
                    change = !(oldValue == null); // If old value is null, no change, otherwise change.
                } else {
                    change = !(intValue.equals(oldValue));
                }
                if(change) {
                    insertIntegerValueChange(key, time, intValue, data);
                }
                if(field.getImmutable() && change) {
                    if(oldValue != null) {
                        // TODO: Return some sort of notification that non empty immutable value was about to change. For now reverse change.
                        change = false;
                    }
                }
                return change;
            }
            // TODO: Add comparisons for rest of the types.
            default:
                // TODO: Unhandled type, log error
                break;
        }
        // Default is that no changes has happened and nothing needs to be done.
        return false;
    }

    private static boolean doContainerChanges(String key, TransferObject so, DateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() != FieldType.CONTAINER) {
            // If this field's type is something other than CONTAINER return false.
            // This method is meant exclusively for handling containers and nothing else.
            return false;
        }
        return false;
    }

    private static void insertStringValueChange(String key, DateTime time, String value, RevisionData data) {
        ValueFieldContainer container = createValueFieldContainer(key, time);
        if(!StringUtils.isEmpty(value)) {
            setSimpleValue(container, value);
        }
        updateData(container, data);
    }

    private static void insertIntegerValueChange(String key, DateTime time, Integer value, RevisionData data) {
        ValueFieldContainer container = createValueFieldContainer(key, time);
        if(value != null) {
            setSimpleValue(container, value.toString());
        }
        updateData(container, data);
    }

    private static void updateData(ValueFieldContainer container, RevisionData data) {
        // TODO: Improve by taking out unnecessary cases such as no original value and new value has operation REMOVED.
        ValueFieldChange change = updateValueField(data, container.getKey(), container);
        data.putChange(change);
    }
}
