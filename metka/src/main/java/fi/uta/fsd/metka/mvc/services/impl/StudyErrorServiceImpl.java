package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyErrorServiceImpl implements StudyErrorService {
    @Autowired
    private StudyErrorsRepository errors;

    @Override public StudyErrorListResponse getStudyErrorList(Long id, Integer no) {
        List<StudyError> list = errors.listErrorsForStudy(id, no);
        StudyErrorListResponse response = new StudyErrorListResponse();
        response.setResult(list.isEmpty()? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL);
        response.setErrors(list);
        return response;
    }

    @Override public ReturnResult insertOrUpdateStudyError(StudyError error) {
        return errors.updateStudyError(error);
    }

    @Override public ReturnResult removeStudyError(Long id) {
        return errors.deleteStudyError(id);
    }
}
