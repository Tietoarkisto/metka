package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.change.Change;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

import static fi.uta.fsd.metka.storage.util.ConversionUtil.stringToLong;

@XmlAccessorType(XmlAccessType.FIELD)
public class SavedDataField extends DataField {
    public static Long valueAsInteger(SavedDataField saved) {
        if(saved == null) {
            return null;
        }
        return saved.valueAsInteger();
    }

    public static String valueAsString(SavedDataField saved) {
        if(saved == null) {
            return "";
        }
        return saved.getActualValue();
    }
    @XmlElement private SavedValue originalValue;
    @XmlElement private SavedValue modifiedValue;

    @JsonCreator
    public SavedDataField(@JsonProperty("key") String key) {
        super(key);
    }

    public SavedValue getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(SavedValue originalValue) {
        this.originalValue = originalValue;
    }

    public SavedValue getModifiedValue() {
        return modifiedValue;
    }

    public void setModifiedValue(SavedValue modifiedValue) {
        this.modifiedValue = modifiedValue;
    }

    /**
     * Builder method
     * @param key
     * @return
     */
    @JsonIgnore
    public static SavedDataField build(String key) {
        return new SavedDataField(key);
    }

    @JsonIgnore
    public boolean hasOriginalValue() {
        return (originalValue != null && originalValue.hasValue());
    }

    @JsonIgnore
    public boolean hasModifiedValue() {
        return (modifiedValue != null && modifiedValue.hasValue());
    }

    /**
     * Convenience method for getting an up to date value.
     * If there exists a modified value then return that, otherwise return original value.
     * @return SavedValue, either modified value if exists or original value. Can return null if both are null.
     */
    @JsonIgnore
    public SavedValue getValue() {
        return (modifiedValue != null) ? modifiedValue : originalValue;
    }

    /**
     * Convenience method for setting the modified value (the value that is most often set).
     * Creates a new Simple value with provided value and sets modified value to that.
     * Returns this instance to facilitate chaining
     * @param value String to be set to SimpleValue
     * @param time LocalDateTime object to give to SimpleValue. Can be null in which case a new time instance is created
     * @param changeMap Map of changes that will contain a change notification for this field
     * @return reference to this instance
     */
    @JsonIgnore
    public SavedDataField setValueToSimple(String value, LocalDateTime time, Map<String, Change> changeMap) {
        if(time == null) {
            time = new LocalDateTime();
        }
        this.modifiedValue = SavedValue.build(time).setToSimpleValue(value);
        changeMap.put(getKey(), new Change(getKey()));
        return this;
    }

    /**
     * Convenience method for checking if this SavedDataField has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(getValue() == null) {
            return false;
        } else return getValue().hasValue();
    }

    /**
     * Convenience method for checking if the most recent value on this SavedDataField equals the given value.
     * NOTICE: Returns false if there is no value.
     *
     * @param compare - Value to compare
     * @return True if the value in this SavedDataField equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueEquals(String compare) {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return getActualValue().equals(compare);
        } else return false;
    }

    /**
     * Convenience method for returning the actual value in this SavedDataField.
     * NOTICE: Returns empty string if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or empty string if value doesn't exist
     */
    @JsonIgnore
    public String getActualValue() {
        String value = "";
        if(hasValue()) {

            value = getValue().getActualValue();
            if(value == null) {
                value = "";
            }
        }
        return value;
    }

    /**
     * If there is a value then assume SimpleValue and return it as a Long.
     * INTEGER type is a description, not an implementation requirement.
     * This is a convenience method and so it assumes you want the most recent value and so it uses
     * getValue() method on SavedDataField to get value.
     * NOTICE:  This does not check the configuration so if the value is DERIVED then only the reference part
     *          of that value gets returned through ConversionUtil.stringToLong conversion.
     * NOTICE:  No matter what the configuration for the field says this method returns ConversionUtil parsed integer.
     *          This can return null.
     * @return Integer gained by using ConversionUtil.stringToInteger on the simple value representation if value is found.
     */
    @JsonIgnore public Long valueAsInteger() {
        Long number = null;
        if(hasValue()) {
            number = stringToLong(getActualValue());
        }

        return number;
    }

    @Override
    public DataField copy() {
        SavedDataField field = new SavedDataField(getKey());
        field.setModifiedValue((modifiedValue != null) ? modifiedValue.copy() : null);
        field.setOriginalValue((originalValue != null) ? originalValue.copy() : null);
        return field;
    }

    @Override
    public void normalize() {
        // If there's no modified value then don't do anything
        if(modifiedValue != null) {
            // Set original value to current value.
            // If modified value has something other than null then it is a valid changed value.
            // Otherwise we're just setting original value to itself, which doesn't really concern us that much.
            originalValue = modifiedValue;
            modifiedValue = null;
        }
    }
}
