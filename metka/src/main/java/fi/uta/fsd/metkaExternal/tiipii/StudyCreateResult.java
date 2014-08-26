package fi.uta.fsd.metkaExternal.tiipii;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class StudyCreateResult {
    private final ReturnResult result;
    private final String studyId;

    public StudyCreateResult(ReturnResult result, String studyId) {
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
