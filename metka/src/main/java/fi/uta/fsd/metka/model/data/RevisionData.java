package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.DataFieldOperator;
import fi.uta.fsd.metka.model.access.calls.DataFieldCall;
import fi.uta.fsd.metka.model.access.calls.DataFieldCallBase;
import fi.uta.fsd.metka.model.access.enums.ConfigCheck;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/data/uml_json_data.graphml
 */
public class RevisionData implements Comparable<RevisionData>, ModelBase, DataFieldContainer {

    // Class
    private final RevisionKey key;
    private final ConfigurationKey configuration;
    private final Map<String, Change> changes = new HashMap<>();
    private final Map<String, DataField> fields = new HashMap<>();
    private RevisionState state;
    private String handler;
    private final Map<Language, ApproveInfo> approved = new HashMap<>();
    private DateTimeUserPair saved;

    @JsonIgnore private DataFieldContainer parent; // This is used for restrictions when validation jumps through reference barrier.

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

    public Map<Language, ApproveInfo> getApproved() {
        return approved;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public String getHandler() {
        return (handler == null) ? "" : handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public DataFieldContainer getParent() {
        return parent;
    }

    @Override
    public void setParent(DataFieldContainer parent) {
        this.parent = parent;
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

    @JsonIgnore public ApproveInfo approveInfoFor(Language language) {
        return approved.get(language);
    }
    @JsonIgnore public boolean isApprovedFor(Language language) {
        return approveInfoFor(language) != null && approveInfoFor(language).getApproved() != null && approveInfoFor(language).getApproved().getTime() != null;
    }

    @JsonIgnore public void approveRevision(Language language, ApproveInfo info) {
        approved.put(language, info);
    }

    @JsonIgnore public void initParents(DataFieldContainer parent) {
        setParent(parent);
        for(DataField field : fields.values()) {
            field.initParents(this);
        }
    }

    @JsonIgnore public void initParents() {
        initParents(null);
    }

    public RevisionData putChange(Change change) {
        if(change.getKey() != null) {
            changes.put(change.getKey(), change);
        }
        return this;
    }

    @JsonIgnore public RevisionKey getRevisionKey() {
        return getKey();
    }

    @JsonIgnore public ConfigurationKey getConfigurationKey() {
        return getConfiguration();
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
