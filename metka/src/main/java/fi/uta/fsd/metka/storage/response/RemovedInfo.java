package fi.uta.fsd.metka.storage.response;

import org.joda.time.LocalDateTime;

public class RemovedInfo {
    public static final RemovedInfo FALSE = new RemovedInfo();

    private final Boolean removed;
    private final LocalDateTime removedAt;
    private final String removedBy;

    public RemovedInfo() {
        removed = false;
        removedAt = null;
        removedBy = null;
    }

    public RemovedInfo(Boolean removed, LocalDateTime removedAt, String removedBy) {
        this.removed = removed;
        this.removedAt = removedAt;
        this.removedBy = removedBy;
    }

    public Boolean getRemoved() {
        return (removed == null) ? false : removed;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public String getRemovedBy() {
        return (removedBy == null) ? "" : removedBy;
    }
}
