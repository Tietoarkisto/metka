package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class RowContainer extends FieldContainer {
    @XmlElement private final Integer rowId;
    @XmlElement private final Map<String, FieldContainer> fields = new HashMap<>();
    @XmlElement private DateTime savedAt;
    @XmlElement private String savedBy;

    @JsonCreator
    public RowContainer(@JsonProperty("key")String key, @JsonProperty("rowId")Integer rowId) {
        super(key);
        this.rowId = rowId;
    }

    public Integer getRowId() {
        return rowId;
    }

    public DateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(DateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public Map<String, FieldContainer> getFields() {
        return fields;
    }

    public FieldContainer getField(String key) {
        return fields.get(key);
    }

    public void putField(FieldContainer field) {
        fields.put(field.getKey(), field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RowContainer that = (RowContainer) o;

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

    @Override
    public FieldContainer copy() {
        RowContainer row = new RowContainer(getKey(), rowId);
        row.setSavedAt(new DateTime(savedAt));
        row.setSavedBy(savedBy);
        for(FieldContainer field : fields.values()) {
            row.putField(field.copy());
        }
        return row;
    }
}
