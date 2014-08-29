package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class APIStudyCreateResponse {
    private final ReturnResult result;
    private final String studyId;

    public APIStudyCreateResponse(ReturnResult result, String studyId) {
        this.result = result;
        this.studyId = studyId;
    }

    public ReturnResult getResult() {
        return result;
    }

    public String getStudyId() {
        return studyId;
    }
}
