package fi.uta.fsd.metka.transfer.study;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;

import java.util.ArrayList;
import java.util.List;

public class StudyErrorsResponse {
    private final ReturnResult result;
    private final List<RevisionSearchResult> rows = new ArrayList<>();

    public StudyErrorsResponse(ReturnResult result) {
        this.result = result;
    }

    public ReturnResult getResult() {
        return result;
    }

    public List<RevisionSearchResult> getRows() {
        return rows;
    }
}
