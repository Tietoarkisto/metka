package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.value.SimpleValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SavedDataField extends DataField {
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
     * Returns this instance to facilitate chaining
     * @param value SavedValue to be set to modified value
     * @return reference to this instance
     */
    @JsonIgnore
    public SavedDataField setValue(SavedValue value) {
        this.modifiedValue = value;
        return this;
    }

    /**
     * Convenience method for checking if this SavedDataField has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(getValue() == null || getValue().getValue() == null) {
            return false;
        } else return true;
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
     * NOTICE: Returns null if hasValue returns false or if the actual value is null.
     *
     * @return String containing the actual value or null if value doesn't exist
     */
    @JsonIgnore
    public String getActualValue() {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return ((SimpleValue)getValue().getValue()).getValue();
        } else return null;
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
