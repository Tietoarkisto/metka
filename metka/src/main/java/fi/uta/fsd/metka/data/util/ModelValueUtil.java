package fi.uta.fsd.metka.data.util;

import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.data.container.SavedValue;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import org.joda.time.LocalDateTime;

import java.util.Map;

import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

// TODO: Refactor to remove functionality that can be done using ModelFieldUtil
public final class ModelValueUtil {
    // Private constructor to stop instantiation
    private ModelValueUtil() {}

    /**
     * Updates the value in given Container using typed procedures
     * @param container SavedDataField to be modified
     * @param field Configuration for given container
     * @param value New value to be inserted
     * @param time Requested time for modification. If not present then current time is used
     */
    public static void updateFieldValue(SavedDataField container, Field field, Object value, LocalDateTime time, Map<String, Change> changeMap) {
        // Sanity check to see that container exists and configuration is for right container
        if(container == null || !container.getKey().equals(field.getKey())) {
            return;
        }
        if(time == null) {
            time = new LocalDateTime();
        }
        switch (field.getType()) {
            case INTEGER:
                Long intValue = stringToLong(value);
                container.setValueToSimple(intValue.toString(), time, changeMap);
                break;
            default:
                container.setValueToSimple((String)value, time, changeMap);
                break;
        }
    }

    /**
     * If there is a value then assume SimpleValue and return it as an Long.
     * INTEGER type is a description, not an implementation requirement.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedDataField to get value.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned through ConversionUtil.stringToLong conversion.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed integer.
     *          This can return null.
     * @param field SavedDataField from where the value is extracted from.
     * @return Integer gained by using ConversionUtil.stringToInteger on the simple value representation if value is found.
     */
    public static Long extractIntegerSimpleValue(SavedDataField field) {
        Long number = null;
        if(field != null && field.hasValue()) {
            number = stringToLong(field.getActualValue());
        }

        return number;
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
