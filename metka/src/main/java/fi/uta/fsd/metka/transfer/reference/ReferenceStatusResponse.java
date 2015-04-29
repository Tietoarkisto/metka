package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.Map;

public class ReferenceStatusResponse {
    public static ReferenceStatusResponse returnResultResponse(ReturnResult result) {
        return new ReferenceStatusResponse(result.name(), false, null, null, null, null);
    }

    private final String result;
    private final boolean exists;
    private final DateTimeUserPair removed;
    private final DateTimeUserPair saved;
    private final Map<Language, ApproveInfo> approved;
    private final UIRevisionState state;

    public ReferenceStatusResponse(String result, boolean exists, DateTimeUserPair removed, DateTimeUserPair saved, Map<Language, ApproveInfo> approved, UIRevisionState state) {
        this.result = result;
        this.exists = exists;
        this.removed = removed;
        this.saved = saved;
        this.approved = approved;
        this.state = state;
    }

    public String getResult() {
        return result;
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

    public UIRevisionState getState() {
        return state;
    }
}
