package fi.uta.fsd.metka.transfer.reference;

import java.util.ArrayList;
import java.util.List;

public class ReferencePathGroupResponse {
    private final String key;
    private final List<ReferencePathResponse> responses = new ArrayList<>();

    public ReferencePathGroupResponse(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public List<ReferencePathResponse> getResponses() {
        return responses;
    }
}
