package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class APIRevisionReadResponse {
    private final ReturnResult result;
    private final RevisionData revision;

    public APIRevisionReadResponse(ReturnResult result, RevisionData revision) {
        this.result = result;
        this.revision = revision;
    }

    public ReturnResult getResult() {
        return result;
    }

    public RevisionData getRevision() {
        return revision;
    }
}
