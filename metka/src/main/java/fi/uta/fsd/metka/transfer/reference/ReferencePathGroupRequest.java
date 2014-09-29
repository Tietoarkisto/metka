package fi.uta.fsd.metka.transfer.reference;

import java.util.List;

public class ReferencePathGroupRequest {
    private String key;
    private List<ReferencePathRequest> requests;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ReferencePathRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ReferencePathRequest> requests) {
        this.requests = requests;
    }
}
