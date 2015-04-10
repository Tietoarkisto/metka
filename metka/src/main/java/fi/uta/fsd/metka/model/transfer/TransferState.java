package fi.uta.fsd.metka.model.transfer;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.general.ApproveInfo;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

import java.util.HashMap;
import java.util.Map;
/**
 * Specification and documentation is found from uml/uml_json_transfer.graphml
 */
public class TransferState {
    private DateTimeUserPair removed;
    private final Map<Language, ApproveInfo> approved = new HashMap<>();
    private UIRevisionState uiState;
    private String handler = "";
    private DateTimeUserPair saved;

    public DateTimeUserPair getRemoved() {
        return removed;
    }

    public void setRemoved(DateTimeUserPair removed) {
        this.removed = removed;
    }

    public Map<Language, ApproveInfo> getApproved() {
        return approved;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public DateTimeUserPair getSaved() {
        return saved;
    }

    public void setSaved(DateTimeUserPair saved) {
        this.saved = saved;
    }

    public UIRevisionState getUiState() {
        return uiState;
    }

    public void setUiState(UIRevisionState uiState) {
        this.uiState = uiState;
    }
}
