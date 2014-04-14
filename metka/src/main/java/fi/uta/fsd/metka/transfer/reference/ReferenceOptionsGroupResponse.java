package fi.uta.fsd.metka.transfer.reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Response for group request for collecting reference options
 */
public class ReferenceOptionsGroupResponse {
    private final List<ReferenceOptionsResponse> responses = new ArrayList<>();

    public List<ReferenceOptionsResponse> getResponses() {
        return responses;
    }
}
