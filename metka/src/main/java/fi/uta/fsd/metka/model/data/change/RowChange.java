package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data_changes.graphml
 */
public class RowChange {
    private final Integer rowId;
    private final Map<String, Change> changes = new HashMap<>();

    @JsonCreator
    public RowChange(@JsonProperty("rowId") Integer rowId) {
        this.rowId = rowId;
    }

    public Integer getRowId() {
        return rowId;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    // Helpers
    @JsonIgnore
    public void putChange(Change change) {
        changes.put(change.getKey(), change);
    }

    @JsonIgnore
    public boolean hasChange(String key) {
        return changes.get(key) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowChange rowChange = (RowChange) o;

        if (!rowId.equals(rowChange.rowId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rowId.hashCode();
    }
}
