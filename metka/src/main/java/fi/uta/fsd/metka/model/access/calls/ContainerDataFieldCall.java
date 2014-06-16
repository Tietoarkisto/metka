package fi.uta.fsd.metka.model.access.calls;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;

public final class ContainerDataFieldCall extends DataFieldCallBase<ContainerDataField> {
    // GET factories
    public static ContainerDataFieldCall get(String key) {
        return new ContainerDataFieldCall(key, CallType.GET);
    }

    // SET factories
    public static ContainerDataFieldCall set(String key) {
        return new ContainerDataFieldCall(key, CallType.SET);
    }

    public static ContainerDataFieldCall set(String key, RevisionData revision) {
        return (ContainerDataFieldCall)set(key).setChangeMap(revision.getChanges());
    }

    private ContainerDataFieldCall(String key, CallType callType) {
        super(DataFieldType.CONTAINER_DATA_FIELD, key, callType);
    }
}
