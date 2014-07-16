package fi.uta.fsd.metka.transfer.expert;

import java.util.ArrayList;
import java.util.List;

public class ExpertSearchQueryResponse {
    private ExpertSearchOperation operation;
    private final List<ExpertSearchRevisionQueryResult> results = new ArrayList<>();

    public ExpertSearchOperation getOperation() {
        return operation;
    }

    public void setOperation(ExpertSearchOperation operation) {
        this.operation = operation;
    }

    public List<ExpertSearchRevisionQueryResult> getResults() {
        return results;
    }
}
