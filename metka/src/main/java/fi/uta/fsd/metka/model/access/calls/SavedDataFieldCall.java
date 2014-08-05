package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.model.data.container.SavedDataField;

public final class SavedDataFieldCall extends DataFieldCallBase<SavedDataField> {

    // GET factories
    public static SavedDataFieldCall get(String key) {
        return new SavedDataFieldCall(key, CallType.GET);
    }

    // SET factories
    public static SavedDataFieldCall set(String key) {
        return new SavedDataFieldCall(key, CallType.SET);
    }

    public static SavedDataFieldCall check(String key) {
        return new SavedDataFieldCall(key, CallType.CHECK);
    }

    private SavedDataFieldCall(String key, CallType callType) {
        super(DataFieldType.SAVED_DATA_FIELD, key, callType);
    }
}
