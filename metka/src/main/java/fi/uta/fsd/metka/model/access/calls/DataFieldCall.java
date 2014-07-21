package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.joda.time.LocalDateTime;

import java.util.Map;

public interface DataFieldCall<T extends DataField> {
    public static enum CallType {
        GET,
        CHECK,
        SET
    }

    public static enum DataFieldType {
        SAVED_DATA_FIELD,
        CONTAINER_DATA_FIELD,
        REFERENCE_CONTAINER_DATA_FIELD
    }

    public CallType getCallType();
    public String getKey();
    public String getValue();
    public Configuration getConfiguration();
    public Map<String, Change> getChangeMap();
    public LocalDateTime getTime();
    public DataFieldType getFieldType();
}
