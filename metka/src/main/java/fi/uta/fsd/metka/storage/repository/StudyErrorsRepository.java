package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyErrorsRepository {
    public List<StudyError> listErrorsForStudy(Long studyId);
    public Pair<ReturnResult, StudyError> loadStudyError(Long id);
    @Transactional(readOnly = false) public ReturnResult updateStudyError(StudyError error);
    @Transactional(readOnly = false) public ReturnResult deleteStudyError(Long id);

}
