package fi.uta.fsd.metka.transfer.study;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class StudyVariablesStudiesResponse {
    private ReturnResult result;
    private final List<StudyVariablesStudyPair> studies = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<StudyVariablesStudyPair> getStudies() {
        return studies;
    }
}
