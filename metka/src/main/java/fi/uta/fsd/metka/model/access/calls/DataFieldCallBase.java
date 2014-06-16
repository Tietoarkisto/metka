package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import org.joda.time.LocalDateTime;

import java.util.Map;

public abstract class DataFieldCallBase<T extends DataField> implements DataFieldCall<T> {
    private final CallType callType;
    private final String key;
    private String value;
    private Configuration configuration;
    private Map<String, Change> changeMap;
    private LocalDateTime time;
    private final DataFieldType fieldType;

    private boolean isValueSet = false;

    protected DataFieldCallBase(DataFieldType fieldType, String key, CallType callType) {
        this.fieldType = fieldType;
        this.key = key;
        this.callType = callType;
    }

    // Getters
    public CallType getCallType() {return callType;}
    public String getKey() {return key;}
    public String getValue() {return value;}
    public Configuration getConfiguration() {return configuration;}
    public Map<String, Change> getChangeMap() {return changeMap;}
    public LocalDateTime getTime() {return time;}
    public DataFieldType getFieldType() {return fieldType;}

    // Setters
    public DataFieldCallBase<T> setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Makes value immutable.
     * After the first time setValue is called subsequent calls do nothing.
     * @param value Value to be set to this Call object
     * @return
     */
    public DataFieldCallBase<T> setValue(String value) {
        if(isValueSet) return this;

        this.value = value;
        isValueSet = true;
        return this;
    }

    public DataFieldCallBase<T> setChangeMap(Map<String, Change> changeMap) {
        this.changeMap = changeMap;
        return this;
    }

    public DataFieldCallBase<T> setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }
}
