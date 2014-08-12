package fi.uta.fsd.metka.transfer.study;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.List;

public class StudyErrorListResponse {
    private ReturnResult result;
    private List<StudyError> errors;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<StudyError> getErrors() {
        return errors;
    }

    public void setErrors(List<StudyError> errors) {
        this.errors = errors;
    }
}
