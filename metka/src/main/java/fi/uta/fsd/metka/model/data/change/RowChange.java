package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.data.RowIdentity;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class RowChange {
    @XmlElement private final RowIdentity key;
    @XmlElement private final Map<String, FieldChange> changes = new HashMap<>();
    @XmlElement private DateTime savedAt;
    @XmlElement private String savedBy;

    @JsonCreator
    public RowChange(@JsonProperty("key")RowIdentity key) {
        this.key = key;
    }

    public RowIdentity getKey() {
        return key;
    }

    public Map<String, FieldChange> getChanges() {
        return changes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowChange rowChange = (RowChange) o;

        if (!key.equals(rowChange.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
