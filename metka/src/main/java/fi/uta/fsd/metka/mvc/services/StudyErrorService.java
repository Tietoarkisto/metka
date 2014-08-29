package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_STUDY_ERRORS +"', 'PERMISSION')")
public interface StudyErrorService {
    StudyErrorListResponse getStudyErrorList(Long id, Integer no);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_STUDY_ERRORS +"', 'PERMISSION')")
    ReturnResult insertOrUpdateStudyError(StudyError error);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_REMOVE_STUDY_ERRORS +"', 'PERMISSION')")
    ReturnResult removeStudyError(Long id);
}
