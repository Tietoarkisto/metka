package fi.uta.fsd.metka.model.data.container;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;

public class ContainerRow {

    private final String key;
    private final Integer rowId;
    private Boolean removed = false;
    private DateTimeUserPair saved;
    private Boolean unapproved = false;

    @JsonCreator
    public ContainerRow(@JsonProperty("key") String key, @JsonProperty("rowId") Integer rowId) {
        this.key = key;
        this.rowId = rowId;
    }

    public String getKey() {
        return key;
    }

    public Integer getRowId() {
        return rowId;
    }

    public Boolean getRemoved() {
        return (removed == null ? false : removed);
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null ? false : removed);
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public Boolean getUnapproved() {
        return unapproved == null ? false : unapproved;
    }

    public void setUnapproved(Boolean unapproved) {
        this.unapproved = unapproved == null ? false : unapproved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContainerRow that = (ContainerRow) o;

        if (!key.equals(that.key)) return false;
        if (!rowId.equals(that.rowId)) return false;

        return true;
    }

    public StatusCode changeStatusFor(Language language, boolean newStatus, Map<String, Change> changeMap, DateTimeUserPair info) {
        if(changeMap == null || removed == newStatus) {
            return StatusCode.NO_CHANGE_IN_VALUE;
        }

        removed = newStatus;
        ContainerChange containerChange = (ContainerChange)changeMap.get(getKey());
        if(containerChange == null) {
            containerChange = new ContainerChange(getKey());
            changeMap.put(getKey(), containerChange);
        }
        if(containerChange.get(getRowId()) == null) {
            containerChange.put(language, new RowChange(getRowId()));
        }
        setSaved(info);
        return StatusCode.ROW_CHANGE;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + rowId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+getKey()+", rowId="+rowId+"]";
    }
}
