package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerChange extends Change {
    @XmlElement private final Map<Integer, RowChange> rows = new HashMap<>();

    @JsonCreator
    public ContainerChange(@JsonProperty("key") String key) {
        super(key);
    }

    public Map<Integer, RowChange> getRows() {
        return rows;
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
