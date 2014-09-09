package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.model.general.DateTimeUserPair;

public class ReferenceStatusResponse {
    private final boolean exists;
    private final DateTimeUserPair removed;

    public ReferenceStatusResponse(boolean exists, DateTimeUserPair removed) {
        this.exists = exists;
        this.removed = removed;
    }

    public boolean isExists() {
        return exists;
    }

    public DateTimeUserPair getRemoved() {
        return removed;
    }
}
