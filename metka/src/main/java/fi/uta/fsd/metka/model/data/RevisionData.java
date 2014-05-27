package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.ModelBase;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "revisionData")
public class RevisionData implements Comparable<RevisionData>, ModelBase {
    // Class
    @XmlElement private final RevisionKey key;
    @XmlElement private final ConfigurationKey configuration;
    @XmlElement private final Map<String, Change> changes = new HashMap<>();
    @XmlElement private final Map<String, DataField> fields = new HashMap<>();
    @XmlElement private RevisionState state;
    @XmlElement private Integer rowIdSeq;
    @XmlElement private LocalDateTime approvalDate;
    @XmlElement private LocalDateTime lastSave;
    @XmlElement private String approver;

    @JsonCreator
    public RevisionData(@JsonProperty("key")RevisionKey key, @JsonProperty("configuration")ConfigurationKey configuration, @JsonProperty("rowIdSeq") Integer rowIdSeq) {
        this.key = key;
        this.configuration = configuration;
        this.rowIdSeq = rowIdSeq;
    }

    public RevisionKey getKey() {
        return key;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public Map<String, Change> getChanges() {
        return changes;
    }

    public Map<String, DataField> getFields() {
        return fields;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDateTime getLastSave() {
        return lastSave;
    }

    public void setLastSave(LocalDateTime lastSave) {
        this.lastSave = lastSave;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public Integer getRowIdSeq() {
        return rowIdSeq;
    }

    // Helper methods
    @JsonIgnore public Change getChange(String key) {
        return changes.get(key);
    }
    @JsonIgnore public Change getChange(Field field) {
        return getChange(field.getKey());
    }
    @JsonIgnore public RevisionData putChange(DataField field) {

        return this;
    }
    @JsonIgnore public RevisionData putChange(Change change) {
        changes.put(change.getKey(), change);
        return this;
    }
    @JsonIgnore public DataField getField(String key) {
        return fields.get(key);
    }
    @JsonIgnore public DataField getField(Field field) {
        return getField(field.getKey());
    }
    @JsonIgnore public RevisionData putField(DataField field) {
        fields.put(field.getKey(), field);
        return this;
    }
    @JsonIgnore public Integer getNewRowId() {
        rowIdSeq++;
        return rowIdSeq;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionData that = (RevisionData) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(RevisionData o) {
        return key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
