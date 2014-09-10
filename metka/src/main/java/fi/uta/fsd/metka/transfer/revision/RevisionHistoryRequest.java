package fi.uta.fsd.metka.transfer.revision;

import java.util.ArrayList;
import java.util.List;

public class RevisionHistoryRequest {
    private final Long id;
    private final List<String> requestFields = new ArrayList<>();

    public RevisionHistoryRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<String> getRequestFields() {
        return requestFields;
    }
}
