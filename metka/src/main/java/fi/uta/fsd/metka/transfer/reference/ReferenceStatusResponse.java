package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.Map;

public class ReferenceStatusResponse {
    private final boolean exists;
    private final DateTimeUserPair removed;
    private final DateTimeUserPair saved;
    private final Map<Language, ApproveInfo> approved;

    public ReferenceStatusResponse(boolean exists, DateTimeUserPair removed, DateTimeUserPair saved, Map<Language, ApproveInfo> approved) {
        this.exists = exists;
        this.removed = removed;
        this.saved = saved;
        this.approved = approved;
    }

    public boolean isExists() {
        return exists;
    }

    public DateTimeUserPair getRemoved() {
        return removed;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public Map<Language, ApproveInfo> getApproved() {
        return approved;
    }
}
