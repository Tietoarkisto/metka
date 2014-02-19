package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.ValueFieldChange;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;
import org.joda.time.DateTime;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/7/14
 * Time: 9:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ModelAccessUtil {

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
     * If there are values then assume SimpleValue and return the first one as a string.
     * NOTICE:  This does not check the configuration so if there is a maxValue attribute that differs from 1
     *          then only the first value gets returned and if the value is DERIVED then only the reference part
     *          of that value gets returned.
     * NOTICE:  No matter what the configuration for the field says this method returns NumberUtils parsed integer.
     * @param field Container from where the value is extracted from.
     * @return Integer gained by using NumberUtils.parseNumber on the simple value representation of the first value found.
     */
    public static Integer extractIntegerSimpleValue(ValueFieldContainer field) {
        Integer integer = null;
        if(field != null && field.getValues().size() > 0) {
            String value = ((SimpleValue) field.getValues().get(0)).getValue();
            integer = NumberUtils.parseNumber(value, Integer.class);
        }

        return integer;
    }

    /**
     * If there are values then assume SimpleValue and return the first one as an integer.
     * NOTICE:  This does not check the configuration so if there is a maxValue attribute that differs from 1
     *          then only the first value gets returned and if the value is DERIVED then only the reference part
     *          of that value gets returned.
     * NOTICE:  No matter what the configuration for the field says this method returns strings.
     * @param field Container from where the value is extracted from.
     * @return String containing the simple value representation of the first value found.
     */
    public static String extractStringSimpleValue(ValueFieldContainer field) {
        String string = null;
        if(field != null && field.getValues().size() > 0) {
            string = ((SimpleValue) field.getValues().get(0)).getValue();
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
        field.getValues().clear();
        field.getValues().add(new SimpleValue(value));
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
        if(newValue.getValues().size() == 0) {
            change.setOperation(ChangeOperation.REMOVED);
        } else {
            change.setOperation(ChangeOperation.MODIFIED);
        }
        change.setNewField(newValue);
        return change;
    }

    /**
     * Checks to see if new value is different from old value.
     * This should only be used for top level (not subfield to anything) non concat and non container fields since containers,
     * rows and subfields are handled differently.
     *
     * TODO: Handle fields that have maxValues != 1, for now assume only one value.
     * @param key Field key of the value being checked.
     * @param so SimpleObject currently being checked. Since we can use the general getByKey method we need only SimpleObject reference.
     * @param data Original data in the database
     * @param config configuration of the data being validated
     * @return Boolean telling has there been a change in the given value or no
     */
    public static boolean doSingleValueChanges(String key, SimpleObject so, DateTime time, RevisionData data, Configuration config) {
        Field field = config.getField(key);
        if(field.getType() == FieldType.CONCAT || field.getType() == FieldType.CONTAINER || !StringUtils.isEmpty(field.getSubfieldTo())) {
            // If this field is of type CONCAT or CONTAINER or a subfield to something then changes can't be handled here.
            // Container fields are handled separately and have a whole process for each row and subfield.
            // Concat values should be checked at the end just by concatenating the values again and seeing if it's a new value.
            // Subfield changes are not recorded as separate changes but the whole row is marked changed if even one field has changed.
            return false;
        }
        Object value = so.getByKey(key);

        ValueFieldContainer container = getValueFieldContainerFromRevisionData(data, key);
        // If current value is null and either previous container is null or values array is empty then there has been no change
        if(value == null && (container == null || container.getValues().size() == 0)) {
            return false;
        }
        boolean change = false;
        switch(field.getType()) {
            case CHOICE: // Choice can be treated as a string since only choice value is saved and it is saved as a string.
            case STRING: // Assume correctness and treat values as string.
                String strValue = (String)value;
                if(StringUtils.isEmpty(strValue)) { // new value is empty (this checks for empty string as well as null)
                    change = !(container == null || container.getValues().size() == 0); // If old value is empty then no changes has happened
                } else { // new value is not empty
                    if(container == null || container.getValues().size() == 0) { // if old value was empty then there has been a change
                        change = true;
                    } else {
                        // Get old value, for now assume there's only a single value (maxValues = 1)
                        String oldValue = extractStringSimpleValue(container);
                        change = !(strValue.equals(oldValue)); // If the old and new values are equal then no change has happened.
                    }
                }
                if(change) {
                    insertStringValueChange(key, time, strValue, data);
                }

                return change;
            case INTEGER:
                Integer intValue = (Integer)value;
                if(container == null || container.getValues().size() == 0) { // if old value was empty then there has been a change
                    change = true;
                } else {
                    Integer oldValue = extractIntegerSimpleValue(container); // Get old value, assume maxValues = 1
                    change = !(intValue.equals(oldValue));
                }
                if(change) {
                    insertIntegerValueChange(key, time, intValue, data);
                }
                return change;
            // TODO: Add comparisons for rest of the types.
        }
        // Default is that no changes has happened and nothing needs to be done.
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
