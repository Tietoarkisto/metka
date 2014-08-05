package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.DataFieldOperator;
import fi.uta.fsd.metka.model.access.calls.DataFieldCallBase;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "revisionData")
public class RevisionData implements Comparable<RevisionData>, ModelBase, DataFieldContainer {

    /**
     * Initialises new revision from old revision data
     * @param oldData Source revision
     * @param newData Target revision
     */
    public static void newRevisionBuilder(RevisionData oldData, RevisionData newData) {

        for(DataField field : oldData.getFields().values()) {
            newData.fields.put(field.getKey(), field.copy());
        }
        for(DataField field : newData.getFields().values()) {
            field.normalize();
        }
    }

    // Class
    @XmlElement private final RevisionKey key;
    @XmlElement private final ConfigurationKey configuration;
    @XmlElement private final Map<String, Change> changes = new HashMap<>();
    @XmlElement private final Map<String, DataField> fields = new HashMap<>();
    @XmlElement private RevisionState state;
    @XmlElement private LocalDateTime approvalDate;
    @XmlElement private String approvedBy;
    @XmlElement private LocalDateTime lastSaved;
    @XmlElement private String lastSavedBy;
    @XmlElement private String handler;

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

    public String getApprovedBy() {
        return (approvedBy == null) ? "" : approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(LocalDateTime lastSaved) {
        this.lastSaved = lastSaved;
    }

    public String getLastSavedBy() {
        return (lastSavedBy == null) ? "" : lastSavedBy;
    }

    public void setLastSavedBy(String lastSavedBy) {
        this.lastSavedBy = lastSavedBy;
    }

    public String getHandler() {
        return (handler == null) ? "" : handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
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
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    // **************
    // Helper methods
    // **************

    public Change getChange(String key) {
        return changes.get(key);
    }
    public Change getChange(Field field) {
        return getChange(field.getKey());
    }

    public RevisionData putChange(Change change) {
        changes.put(change.getKey(), change);
        return this;
    }

    // *************************
    // Interface implementations
    // *************************

    // Comparable
    @Override
    public int compareTo(RevisionData o) {
        return key.compareTo(o.key);
    }

    // DataFieldContainer

    /**
     * Executes DataField operations on this RevisionData based on the DataFieldCall given.
     * If SET operation is requested and no ChangeMap is provided in call then this RevisionData's change map is provided by default
     * @param call
     * @param <T>
     * @return
     */
    @Override
    public <T extends DataField> Pair<StatusCode, T> dataField(DataFieldCall<T> call) {
        switch(call.getCallType()) {
            case GET:
                return DataFieldOperator.getDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            case CHECK:
                return DataFieldOperator.checkDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            case SET:
                if(call.getChangeMap() == null) ((DataFieldCallBase<T>)call).setChangeMap(getChanges());
                return DataFieldOperator.setDataFieldOperation(getFields(), call, new ConfigCheck[]{ConfigCheck.NOT_SUBFIELD});
            default:
                return new ImmutablePair<>(StatusCode.INCORRECT_PARAMETERS, null);
        }
    }
}
