package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import fi.uta.fsd.metka.transfer.study.StudyErrorResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyErrorService {
    @Autowired
    private StudyErrorsRepository errors;

    public StudyErrorListResponse getStudyErrorList(Long id, Integer no) {
        List<StudyError> list = errors.listErrorsForStudy(id, no);
        StudyErrorListResponse response = new StudyErrorListResponse();
        response.setResult(list.isEmpty()? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL);
        response.setErrors(list);
        return response;
    }

    public StudyErrorResponse getStudyError(Long id) {
        Pair<ReturnResult, StudyError> pair = errors.loadStudyError(id);
        StudyErrorResponse response = new StudyErrorResponse();
        response.setResult(pair.getLeft());
        response.setError(pair.getRight());
        return response;
    }

    public ReturnResult insertOrUpdateStudyError(StudyError error) {
        return errors.updateStudyError(error);
    }

    public ReturnResult removeStudyError(Long id) {
        return errors.deleteStudyError(id);
    }
}
