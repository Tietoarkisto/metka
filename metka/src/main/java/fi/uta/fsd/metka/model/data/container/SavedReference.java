package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.value.SimpleValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Single reference in a reference container is saved through this
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SavedReference {
    @XmlElement private final String key;
    @XmlElement private final Integer rowId;
    @XmlElement private Boolean removed;
    @XmlElement private SavedValue originalValue;
    @XmlElement private SavedValue modifiedValue;

    @JsonCreator
    public SavedReference(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        this.key = key;
        this.rowId = rowId;
    }

    public String getKey() {
        return key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public Boolean isRemoved() {
        return (removed == null ? false : removed);
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null ? false : removed);
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
     * Convinience method for getting an up to date value.
     * If there exists a modified value then return that, otherwise return original value.
     * @return SavedValue, either modified value if exists or original value. Can return null if both are null.
     */
    @JsonIgnore
    public SavedValue getValue() {
        return (modifiedValue != null) ? modifiedValue : originalValue;
    }

    /**
     * Convinience method for checking if this reference has an actual Value
     * @return If there is an actual Value returns true, otherwise false
     */
    @JsonIgnore
    public boolean hasValue() {
        if(getValue() == null || getValue().getValue() == null) {
            return false;
        } else return true;
    }

    /**
     * Convinience method for checking if the most recent value on this reference equals the given value.
     * NOTICE: Returns false if there is no value.
     *
     * @param compare - Value to compare
     * @return True if the value in this reference equals the value given, false otherwise
     */
    @JsonIgnore
    public boolean valueEquals(String compare) {
        if(hasValue()) {
            // Assume saved value is SimpleValue, if there's some change to this later then adapt this method
            return ((SimpleValue)getValue().getValue()).getValue().equals(compare);
        } else return false;
    }

    /**
     * Convenience method for returning the actual value in this SavedReference.
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

    @JsonIgnore
    public SavedReference copy() {
        SavedReference reference = new SavedReference(getKey(), getRowId());
        reference.setModifiedValue((modifiedValue != null) ? modifiedValue.copy() : null);
        reference.setOriginalValue((originalValue != null) ? originalValue.copy() : null);
        return reference;
    }

    public void normalize() {
        // If there's no modifiedValue then don't do anything
        if(modifiedValue != null) {
            // Set original value to current value.
            // If modified value has something other than null then it is a valid changed value.
            originalValue = modifiedValue;
            // Set modified value to null
            modifiedValue = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedReference that = (SavedReference) o;

        if (!key.equals(that.key)) return false;
        if (!rowId.equals(that.rowId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + rowId.hashCode();
        return result;
    }
}
