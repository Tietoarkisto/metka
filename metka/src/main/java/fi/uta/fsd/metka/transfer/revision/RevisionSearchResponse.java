package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class RevisionSearchResponse {
    private ReturnResult result;
    private final List<RevisionSearchResult> rows = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<RevisionSearchResult> getRows() {
        return rows;
    }
}