package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.study.StudyErrorsResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', 'PERMISSION')")
@Transactional(readOnly = true)
public interface StudyService {
    StudyVariablesStudiesResponse collectStudiesWithVariables();

    RevisionSearchResponse collectAttachmentHistory(TransferData transferData);

    StudyErrorsResponse getStudiesWithErrors();
}
