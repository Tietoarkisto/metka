package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data_changes.graphml
 */
public class ContainerChange extends Change {
    private final Map<Integer, RowChange> rows = new HashMap<>();

    @JsonCreator
    public ContainerChange(@JsonProperty("key") String key) {
        super(key);
    }


    public Map<Integer, RowChange> getRows() {
        return rows;
    }

    @JsonIgnore public boolean hasRows() {
        return !rows.isEmpty();
    }

    // Helpers
    @JsonIgnore
    public void put(RowChange row) {
        rows.put(row.getRowId(), row);
    }

    public RowChange get(Integer rowId) {
        return rows.get(rowId);
    }
}
