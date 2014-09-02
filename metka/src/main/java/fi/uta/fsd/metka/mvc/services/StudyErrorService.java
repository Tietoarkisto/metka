package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_STUDY_ERRORS +"', 'PERMISSION')")
@Transactional
public interface StudyErrorService {
    @Transactional(readOnly = true) StudyErrorListResponse getStudyErrorList(Long id);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_STUDY_ERRORS +"', 'PERMISSION')")
    ReturnResult insertOrUpdateStudyError(StudyError error);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_REMOVE_STUDY_ERRORS +"', 'PERMISSION')")
    ReturnResult removeStudyError(Long id);
}
