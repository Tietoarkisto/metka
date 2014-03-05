package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class RowChange extends Change {
    @XmlElement private final Integer rowId;
    @XmlElement private final Map<String, Change> changes = new HashMap<>();

    @JsonCreator
    public RowChange(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        super(key);
        this.rowId = rowId;
    }

    public Integer getRowId() {
        return rowId;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    public void putChange(Change change) {
        changes.put(change.getKey(), change);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowChange rowChange = (RowChange) o;

        if (!getKey().equals(rowChange.getKey())) return false;
        if (!rowId.equals(rowChange.rowId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + rowId.hashCode();
        return result;
    }
}
