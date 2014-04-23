package fi.uta.fsd.metka.transfer.reference;

import java.util.List;

/**
 * Grouped request for multiple different references.
 * This is usually a row in a container or referencecontainer. Because of this a group key is provided.
 * In cases response is directly related to one entity, such as a container, the key provided
 * should be the field key of that entity or some other way of detecting that entity.
 * If no key is provided it is assumed that requests have no relation to each other and should be handled
 * separately.
 */
public class ReferenceOptionsGroupRequest {
    private String key;
    List<ReferenceOptionsRequest> requests;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<ReferenceOptionsRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ReferenceOptionsRequest> requests) {
        this.requests = requests;
    }
}
