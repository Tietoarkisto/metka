package fi.uta.fsd.metka.storage.response;

import fi.uta.fsd.metka.enums.ConfigurationType;
import org.joda.time.LocalDateTime;

public class RevisionableInfo {
    public static final RevisionableInfo FALSE = new RevisionableInfo();

    private final Long id;
    private final ConfigurationType type;
    private final Integer approved;
    private final Integer current;
    private final Boolean removed;
    private final LocalDateTime removedAt;
    private final String removedBy;

    public RevisionableInfo() {
        id = null;
        type = null;
        approved = null;
        current = null;
        removed = false;
        removedAt = null;
        removedBy = null;
    }

    public RevisionableInfo(Long id, ConfigurationType type, Integer approved, Integer current,
                            Boolean removed, LocalDateTime removedAt, String removedBy) {
        this.id = id;
        this.type = type;
        this.approved = approved;
        this.current = current;
        this.removed = removed;
        this.removedAt = removedAt;
        this.removedBy = removedBy;
    }

    public Long getId() {
        return id;
    }

    public ConfigurationType getType() {
        return type;
    }

    public Integer getApproved() {
        return approved;
    }

    public Integer getCurrent() {
        return current;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public String getRemovedBy() {
        return removedBy;
    }
}
