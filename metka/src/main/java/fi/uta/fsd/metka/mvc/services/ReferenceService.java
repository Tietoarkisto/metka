package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.transfer.reference.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ReferenceService {
    ReferenceOption getCurrentFieldOption(Language language, RevisionData data, String path);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    List<ReferenceOption> collectReferenceOptions(ReferenceOptionsRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    ReferenceRowResponse getReferenceRow(ReferenceRowRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    ReferenceStatusResponse getReferenceStatus(Long id);
}