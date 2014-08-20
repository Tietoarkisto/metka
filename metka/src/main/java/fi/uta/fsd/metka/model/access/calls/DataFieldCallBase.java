package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.value.Value;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;

public abstract class DataFieldCallBase<T extends DataField> implements DataFieldCall<T> {
    private final CallType callType;
    private final String key;
    private Value value;
    private Configuration configuration;
    private Map<String, Change> changeMap;
    private DateTimeUserPair info;
    private final DataFieldType fieldType;
    private Language language;

    private boolean isValueSet = false;

    protected DataFieldCallBase(DataFieldType fieldType, String key, CallType callType) {
        this.fieldType = fieldType;
        this.key = key;
        this.callType = callType;
    }

    // Getters
    public CallType getCallType() {return callType;}
    public String getKey() {return key;}
    public Value getValue() {return value;}
    public Configuration getConfiguration() {return configuration;}
    public Map<String, Change> getChangeMap() {return changeMap;}
    public DateTimeUserPair getInfo() {return info;}
    public DataFieldType getFieldType() {return fieldType;}
    public Language getLanguage() {return language;}

    // Setters
    public DataFieldCallBase<T> setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Shortcut to set the value with String.
     * Creates a new Value without derived
     * @param value
     * @return
     */
    public DataFieldCallBase<T> setValue(String value) {
        return setValue(new Value(value, ""));
    }

    /**
     * Makes value immutable.
     * After the first time setValue is called subsequent calls do nothing.
     * @param value Value to be set to this Call object
     * @return
     */
    public DataFieldCallBase<T> setValue(Value value) {
        if(isValueSet) return this;

        this.value = value;
        isValueSet = true;
        return this;
    }

    public DataFieldCallBase<T> setChangeMap(Map<String, Change> changeMap) {
        this.changeMap = changeMap;
        return this;
    }

    public DataFieldCallBase<T> setInfo(DateTimeUserPair info) {
        this.info = info;
        return this;
    }

    public DataFieldCallBase<T> setLanguage(Language language) {
        this.language = language;
        return this;
    }
}
