package fi.uta.fsd.metka.transfer.revision;

import java.util.ArrayList;
import java.util.List;

public class RevisionHistoryRequest {
    private Long id;
    private final List<String> requestFields = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getRequestFields() {
        return requestFields;
    }
}
