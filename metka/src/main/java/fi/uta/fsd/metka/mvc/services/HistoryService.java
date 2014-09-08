package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
// TODO: Implement missing functionality
public interface HistoryService {
    /*List<RevisionSO> getRevisionHistory(Long id);

    ChangeCompareSO compareRevisions(ChangeCompareRequest request);*/
}
