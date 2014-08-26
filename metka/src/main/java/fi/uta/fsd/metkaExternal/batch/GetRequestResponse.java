package fi.uta.fsd.metkaExternal.batch;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class GetRequestResponse {
    private final ReturnResult result;
    private final RevisionData revision;

    public GetRequestResponse(ReturnResult result, RevisionData revision) {
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
