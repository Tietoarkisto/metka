package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.model.data.RevisionData;

public class APIRevisionSaveRequest extends APIRequest {
    private RevisionData revision;

    public RevisionData getRevision() {
        return revision;
    }

    public void setRevision(RevisionData revision) {
        this.revision = revision;
    }
}
