package fi.uta.fsd.metka.transfer.expert;

import java.util.ArrayList;
import java.util.List;

public class ExpertSearchListResponse {
    private final List<SavedExpertSearchItem> queries = new ArrayList<>();

    public List<SavedExpertSearchItem> getQueries() {
        return queries;
    }
}
