package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.data.container.SavedValue;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import static fi.uta.fsd.metka.data.util.ConversionUtil.stringToInteger;

public class ModelValueUtil {
    /**
     * Updates the value in given Container using typed procedures
     * @param container SavedDataField to be modified
     * @param field Configuration for given container
     * @param value New value to be inserted
     * @param time Requested time for modification. If not present then current time is used
     */
    public static void updateFieldValue(SavedDataField container, Field field, Object value, LocalDateTime time) {
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
    public static void insertStringValueChange(LocalDateTime time, String value, SavedDataField container) {
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
    public static void insertIntegerValueChange(LocalDateTime time, Integer value, SavedDataField container) {
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
    public static void updateSavedDataField(SavedDataField container, SavedValue saved) {
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
}
