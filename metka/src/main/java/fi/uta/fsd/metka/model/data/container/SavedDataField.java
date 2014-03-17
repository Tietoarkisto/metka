package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
     * Convinience method for getting an up to date value.
     * If there exists a modified value then return that, otherwise return original value.
     * @return SavedValue, either modified value if exists or original value. Can return null if both are null.
     */
    @JsonIgnore
    public SavedValue getValue() {
        return (modifiedValue != null) ? modifiedValue : originalValue;
    }

    @Override
    public DataField copy() {
        SavedDataField field = new SavedDataField(getKey());
        field.setModifiedValue((modifiedValue != null) ? modifiedValue.copy() : null);
        field.setOriginalValue((originalValue != null) ? originalValue.copy() : null);
        return field;
    }
}
