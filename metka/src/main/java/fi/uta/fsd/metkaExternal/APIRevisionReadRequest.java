package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.model.general.RevisionKey;

public class APIRevisionReadRequest extends APIRequest {
    private RevisionKey key;

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }
}
