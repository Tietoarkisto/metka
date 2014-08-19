package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.data.value.Value;

public final class ValueDataFieldCall extends DataFieldCallBase<ValueDataField> {

    // GET factories
    public static ValueDataFieldCall get(String key) {
        return new ValueDataFieldCall(key, CallType.GET);
    }

    // SET factories
    public static ValueDataFieldCall set(String key, Value value, Language language) {
        return (ValueDataFieldCall)(new ValueDataFieldCall(key, CallType.SET)).setValue(value).setLanguage(language);
    }

    public static ValueDataFieldCall check(String key, Value value, Language language) {
        return (ValueDataFieldCall)(new ValueDataFieldCall(key, CallType.CHECK)).setValue(value).setLanguage(language);
    }

    private ValueDataFieldCall(String key, CallType callType) {
        super(DataFieldType.SAVED_DATA_FIELD, key, callType);
    }
}
