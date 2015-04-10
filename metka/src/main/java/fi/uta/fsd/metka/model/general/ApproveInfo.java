package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

public class ApproveInfo {

    private final Integer revision;
    private final DateTimeUserPair approved;

    @JsonCreator
    public ApproveInfo(@JsonProperty("revision")Integer revision, @JsonProperty("approved")DateTimeUserPair approved) {
        this.revision = revision;
        this.approved = approved;
    }

    public Integer getRevision() {
        return revision;
    }

    public DateTimeUserPair getApproved() {
        return approved;
    }
}