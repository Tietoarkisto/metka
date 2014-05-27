package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.util.ModelAccessUtil;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Single row of fields in a container is saved through this
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataRow implements ModelAccessUtil.PathNavigable {
    @XmlElement private final String key;
    @XmlElement private final Integer rowId;
    @XmlElement private final Map<String, DataField> fields = new HashMap<>();
    @XmlElement private Boolean removed = false;
    @XmlElement private LocalDateTime savedAt;
    @XmlElement private String savedBy;

    @JsonCreator
    public DataRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
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

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public Map<String, DataField> getFields() {
        return fields;
    }

    // Helper methods
    @JsonIgnore
    public DataField getField(String key) {
        return fields.get(key);
    }

    @JsonIgnore
    public void putField(DataField field) {
        fields.put(field.getKey(), field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DataRow that = (DataRow) o;

        if (!getKey().equals(that.getKey())) return false;
        if (!rowId.equals(that.rowId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getKey().hashCode();
        result = 31 * result + rowId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+getKey()+", rowId="+rowId+"]";
    }

    public DataRow copy() {
        DataRow row = new DataRow(getKey(), rowId);
        row.setSavedAt(new LocalDateTime(savedAt));
        row.setSavedBy(savedBy);
        for(DataField field : fields.values()) {
            row.putField(field.copy());
        }
        return row;
    }

    public void normalize() {
        for(DataField field : fields.values()) {
            field.normalize();
        }
    }
}
