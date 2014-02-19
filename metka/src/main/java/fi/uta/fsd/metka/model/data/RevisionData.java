package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.FieldChange;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "revisionData")
public class RevisionData implements Comparable<RevisionData> {
    // Factories

    public static RevisionData createRevisionData(RevisionEntity entity, ConfigurationKey configuration) {
        RevisionData data = new RevisionData(
                new RevisionKey(entity.getKey().getRevisionableId(), entity.getKey().getRevisionNo()),
                configuration
        );
        data.setState(entity.getState());
        return data;
    }

    // Class
    @XmlElement private final RevisionKey key;
    @XmlElement private final ConfigurationKey configuration;
    @XmlElement private final Map<String, FieldChange> changes = new HashMap<>();
    @XmlElement private final Map<String, FieldContainer> fields = new HashMap<>();
    @XmlElement private RevisionState state;
    @XmlElement private LocalDate approvalDate;
    @XmlElement private LocalDate lastSave;
    @XmlElement private String approver;

    @JsonCreator
    public RevisionData(@JsonProperty("key")RevisionKey key, @JsonProperty("configuration")ConfigurationKey configuration) {
        this.key = key;
        this.configuration = configuration;
    }

    public RevisionKey getKey() {
        return key;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public Map<String, FieldChange> getChanges() {
        return changes;
    }

    public Map<String, FieldContainer> getFields() {
        return fields;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDate getLastSave() {
        return lastSave;
    }

    public void setLastSave(LocalDate lastSave) {
        this.lastSave = lastSave;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    // Helper methods
    public FieldChange getChange(String key) {
        return changes.get(key);
    }
    public FieldChange getChange(Field field) {
        return getChange(field.getKey());
    }
    public void putChange(FieldChange change) {
        changes.put(change.getKey(), change);
    }
    public FieldContainer getField(String key) {
        return fields.get(key);
    }
    public FieldContainer getField(Field field) {
        return getField(field.getKey());
    }
    public void putField(FieldContainer field) {
        fields.put(field.getKey(), field);
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
