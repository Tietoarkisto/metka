package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class RevisionCompareResponse {
    private final ReturnResult result;
    private final List<RevisionCompareResponseRow> rows = new ArrayList<>();

    public RevisionCompareResponse(ReturnResult result) {
        this.result = result;
    }

    public ReturnResult getResult() {
        return result;
    }

    public List<RevisionCompareResponseRow> getRows() {
        return rows;
    }
}
