package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public SavedReference copy() {
        SavedReference reference = new SavedReference(getKey(), getRowId());
        reference.setModifiedValue((modifiedValue != null) ? modifiedValue.copy() : null);
        reference.setOriginalValue((originalValue != null) ? originalValue.copy() : null);
        return reference;
    }
}
