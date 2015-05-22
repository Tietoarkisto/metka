package fi.uta.fsd.metka.transfer.expert;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class ExpertSearchQueryResponse {
    private ReturnResult result;
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

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }
}
