package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.interfaces.TransferFieldContainer;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/uml_json_transfer.graphml
 */
public class TransferData implements ModelBase, TransferFieldContainer {
    private final RevisionKey key;
    private final ConfigurationKey configuration;
    private final Map<String, TransferField> fields = new HashMap<>();
    private final TransferState state = new TransferState();

    @JsonCreator
    public TransferData(@JsonProperty("key")RevisionKey key, @JsonProperty("configuration")ConfigurationKey configuration) {
        this.key = key;
        this.configuration = configuration;
    }

    public RevisionKey getKey() {
        return key;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public Map<String, TransferField> getFields() {
        return fields;
    }

    public TransferState getState() {
        return state;
    }

    @JsonIgnore
    public boolean hasField(String key) {
        return fields.containsKey(key) && fields.get(key) != null;
    }

    @JsonIgnore
    public TransferField getField(String key) {
        return fields.get(key);
    }

    @JsonIgnore
    public void addField(TransferField field) {
        if(hasField(field.getKey())) {
            return;
        }
        fields.put(field.getKey(), field);
    }

    public static TransferData buildFromRevisionData(RevisionData revision, RevisionableInfo info) {
        // Create new TransferData copying the revision data's revision key and configuration key
        // Copying the keys is unnecessary but doesn't hurt either
        TransferData data = new TransferData(revision.getKey().copy(), revision.getConfiguration().copy());

        // Set TransferData state. TransferState has certain initial values so only situations that differ from that state have to be checked.
        if(info.getRemoved()) {
            data.state.setRemoved(new DateTimeUserPair(info.getRemovedAt(), info.getRemovedBy()));
        }
        for(Language language : Language.values()) {
            data.state.getApproved().put(language, revision.getApproved().get(language));
        }
        if(revision.getState() == RevisionState.DRAFT) {
            data.state.setHandler(revision.getHandler());
        }

        data.state.setUiState(info.getRemoved() ? UIRevisionState.REMOVED : UIRevisionState.fromRevisionState(revision.getState()));

        data.state.setSaved(revision.getSaved());

        for(DataField field : revision.getFields().values()) {
            TransferField transferField = TransferField.buildFromDataField(field);
            if(transferField != null) {
                data.fields.put(transferField.getKey(), transferField);
            }
        }

        return data;
    }
}
