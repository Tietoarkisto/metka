package fi.uta.fsd.metka.mvc.domain.simple;

import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.SimpleValue;

import java.util.HashMap;
import java.util.Map;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Move revision data between service and jsp layers
 */
public class TransferObject {
    private Integer id;
    private Integer revision;
    private RevisionState state;
    private ConfigurationKey configuration;

    private final Map<String, Object> values = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
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
     * @param data - Revision from which data is needed.
     * @param config - Configuration of given revision (assumes correct configuration).
     * @return TransferObject containing all relevant data ready for UI
     */
    public static TransferObject buildTransferObjectFromRevisionData(RevisionData data, Configuration config) {
        // check if data is for series
        if(data == null) {
            return null;
        }

        TransferObject to = new TransferObject();
        to.setId(data.getKey().getId());
        to.setRevision(data.getKey().getRevision());
        to.setState(data.getState());
        to.setConfiguration(data.getConfiguration());

        // TODO: this should be automated as much as possible using configuration in the future.
        // TODO: For now assumes that all fields are ValueFields
        for(Field field : config.getFields().values()) {
            if(field.getType() != FieldType.CONTAINER) {
                // Since we don't care about types at the UI that much we can push all values to the map as Strings
                ValueFieldContainer container = getValueFieldContainerFromRevisionData(data, field.getKey());
                if(container != null) {
                    to.setByKey(field.getKey(), ((SimpleValue)container.getValue()).getValue());
                }
            } else {
                // TODO: Handle containers. This needs a recursion since subfields can now be containers.
            }
        }

        return to;
    }
}
