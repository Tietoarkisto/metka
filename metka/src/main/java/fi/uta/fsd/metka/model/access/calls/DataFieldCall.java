package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;

public interface DataFieldCall<T extends DataField> {
    public static enum CallType {
        GET,
        CHECK,
        SET
    }

    public static enum DataFieldType {
        VALUE_DATA_FIELD,
        CONTAINER_DATA_FIELD,
        REFERENCE_CONTAINER_DATA_FIELD
    }

    public CallType getCallType();
    public String getKey();
    public Value getValue();
    public Configuration getConfiguration();
    public Map<String, Change> getChangeMap();
    public DateTimeUserPair getInfo();
    public DataFieldType getFieldType();
    public Language getLanguage();
}
