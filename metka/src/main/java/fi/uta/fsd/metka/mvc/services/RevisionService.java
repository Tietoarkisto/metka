package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', 'PERMISSION')")
public interface RevisionService {
    RevisionDataResponse view(Long id, String type);

    RevisionDataResponse view(Long id, Integer no, String type);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_CREATE_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse create(RevisionCreateRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse edit(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse save(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_APPROVE_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse approve(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_REMOVE_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse remove(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_RESTORE_REVISION +"', 'PERMISSION')")
    RevisionOperationResponse restore(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', 'PERMISSION')")
    RevisionSearchResponse search(RevisionSearchRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', 'PERMISSION')")
    RevisionSearchResponse studyIdSearch(String studyId);
}
