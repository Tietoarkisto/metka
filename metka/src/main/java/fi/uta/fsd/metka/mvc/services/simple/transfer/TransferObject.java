package fi.uta.fsd.metka.mvc.services.simple.transfer;

import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Move revision data between service and jsp layers
 */
public class TransferObject {
    private Long id;
    private Integer revision;
    private UIRevisionState state;
    private ConfigurationKey configuration;
    private String urlHash;

    private final Map<String, Object> values = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public UIRevisionState getState() {
        return state;
    }

    public void setState(UIRevisionState state) {
        this.state = state;
    }

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationKey configuration) {
        this.configuration = configuration;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public String getUrlHash() {
        return urlHash;
    }

    public void setUrlHash(String urlHash) {
        this.urlHash = urlHash;
    }

    public Object getByKey(String key) {
        return values.get(key);
    }

    public void setByKey(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transfer object for "+configuration+" with values [");
        for(String key : values.keySet()) {
            sb.append(key+", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Collect all available data from RevisionData object to be ready to display on UI.
     * @param data - Revision from which data is needed..
     * @return TransferObject containing all relevant data ready for UI
     */
    public static TransferObject buildTransferObjectFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null) {
            return null;
        }

        TransferObject to = new TransferObject();
        to.setId(data.getKey().getId());
        to.setRevision(data.getKey().getNo());
        to.setState(UIRevisionState.fromRevisionState(data.getState()));
        to.setConfiguration(data.getConfiguration());

        for(DataField field : data.getFields().values()) {
            if(field instanceof ContainerDataField) {
                JSONObject ct = ContainerTransfer.buildJSONObject((ContainerDataField)field);
                if(ct != null) {
                    to.setByKey(field.getKey(), ct.toString());
                }
            } else if(field instanceof ReferenceContainerDataField) {
                JSONObject rct = ContainerTransfer.buildJSONObject((ReferenceContainerDataField)field);
                if(rct != null) {
                    to.setByKey(field.getKey(), rct.toString());
                }
            } else {
                // Since we don't care about types at the UI that much we can push all values to the map as Strings
                SavedDataField saved = (SavedDataField)field;
                if(saved != null && saved.hasValue()) {
                    to.setByKey(saved.getKey(), saved.getActualValue());
                }
            }
        }

        return to;
    }
}
