package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.Map;

public class ContainerChange extends Change {
    private final Map<Language, Map<Integer, RowChange>> rows = new HashMap<>();

    @JsonCreator
    public ContainerChange(@JsonProperty("key") String key, @JsonProperty("type") ChangeType type) {
        super(key, type);
    }

    // TODO: Uncomment
    /*public Map<Language, Map<Integer, RowChange>> getRows() {
        return rows;
    }*/

    @JsonIgnore public Map<Integer, RowChange> getRowsFor(Language language) {
        return rows.get(language);
    }

    @JsonIgnore public boolean hasRowsFor(Language language) {
        return rows.get(language) != null && !rows.get(language).isEmpty();
    }

    // Helpers
    @JsonIgnore
    public void put(Language language, RowChange row) {
        if(rows.get(language) == null) {
            rows.put(language, new HashMap<Integer, RowChange>());
        }
        getRowsFor(language).put(row.getRowId(), row);
        setChangeIn(language);
    }

    public RowChange get(Integer rowId) {
        for(Language language : Language.values()) {
            if(hasRowsFor(language) && getRowsFor(language).get(rowId) != null) {
                return getRowsFor(language).get(rowId);
            }
        }
        return null;
    }
}
