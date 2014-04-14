package fi.uta.fsd.metka.transfer.reference;

import java.util.List;

/**
 * Grouped request for multiple different references
 */
public class ReferenceOptionsGroupRequest {
    List<ReferenceOptionsRequest> requests;

    public List<ReferenceOptionsRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ReferenceOptionsRequest> requests) {
        this.requests = requests;
    }
}
