package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional
public interface RevisionService {
    @Transactional(readOnly = true) RevisionDataResponse view(Long id, String type);

    @Transactional(readOnly = true) RevisionDataResponse view(Long id, Integer no, String type);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_CREATE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionOperationResponse create(RevisionCreateRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionOperationResponse edit(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionOperationResponse save(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_APPROVE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionOperationResponse approve(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_REMOVE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionOperationResponse remove(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_RESTORE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionOperationResponse restore(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', '" + PermissionCheck.Values.PERMISSION + "')")
    @Transactional(readOnly = true) RevisionSearchResponse search(RevisionSearchRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', '" + PermissionCheck.Values.PERMISSION + "')")
    @Transactional(readOnly = true) RevisionSearchResponse studyIdSearch(String studyId);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.CLAIM_REVISION + "')")
    ReturnResult claimRevision(RevisionKey key);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.RELEASE_REVISION + "')")
    ReturnResult releaseRevision(RevisionKey key);

    @Transactional(readOnly = true) RevisionSearchResponse collectRevisionHistory(RevisionHistoryRequest request);

    @Transactional(readOnly = true) RevisionCompareResponse revisionCompare(RevisionCompareRequest request);
}
