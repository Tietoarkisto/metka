package fi.uta.fsd.metka.transfer.study;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class StudyErrorResponse {
    private ReturnResult result;
    private StudyError error;

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public ReturnResult getResult() {
        return result;
    }

    public void setError(StudyError error) {
        this.error = error;
    }

    public StudyError getError() {
        return error;
    }
}
