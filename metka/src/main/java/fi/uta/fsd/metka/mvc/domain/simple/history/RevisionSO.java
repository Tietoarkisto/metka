package fi.uta.fsd.metka.mvc.domain.simple.history;

import fi.uta.fsd.metka.data.enums.RevisionState;
import org.joda.time.LocalDateTime;

public class RevisionSO {
    private Long id;
    private Integer revision;
    private LocalDateTime approvalDate;
    private RevisionState state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RevisionSO that = (RevisionSO) o;

        if (!id.equals(that.id)) return false;
        if (!revision.equals(that.revision)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }
}
