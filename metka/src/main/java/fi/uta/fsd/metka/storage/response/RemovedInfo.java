package fi.uta.fsd.metka.storage.response;

import org.joda.time.LocalDateTime;

public class RemovedInfo {
    private Boolean removed;
    private LocalDateTime removedAt;
    private String removedBy;

    public Boolean getRemoved() {
        return (removed == null) ? false : removed;
    }

    public void setRemoved(Boolean removed) {
        this.removed = (removed == null) ? false : removed;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(LocalDateTime removedAt) {
        this.removedAt = removedAt;
    }

    public String getRemovedBy() {
        return (removedBy == null) ? "" : removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }
}
