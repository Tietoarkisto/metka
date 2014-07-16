package fi.uta.fsd.metka.mvc.search;

import fi.uta.fsd.metka.model.data.RevisionData;

public class RevisionDataRemovedContainer {
    private final RevisionData data;
    private final boolean removed;

    public RevisionDataRemovedContainer(RevisionData data, boolean removed) {
        this.data = data;
        this.removed = removed;
    }

    public RevisionData getData() {
        return data;
    }

    public boolean isRemoved() {
        return removed;
    }
}
