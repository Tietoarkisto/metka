package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;

public final class ReferenceContainerDataFieldCall extends DataFieldCallBase<ReferenceContainerDataField> {
    // GET factories
    public static ReferenceContainerDataFieldCall get(String key) {
        return new ReferenceContainerDataFieldCall(key, CallType.GET);
    }

    // SET factories
    public static ReferenceContainerDataFieldCall set(String key) {
        return (new ReferenceContainerDataFieldCall(key, CallType.SET));
    }

    private ReferenceContainerDataFieldCall(String key, CallType callType) {
        super(DataFieldType.REFERENCE_CONTAINER_DATA_FIELD, key, callType);
    }
}
