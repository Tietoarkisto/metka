package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderListResponse;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_BINDER_PAGES +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional
public interface BinderService {
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_BINDER_PAGES +"', '" + PermissionCheck.Values.PERMISSION + "')")
    BinderListResponse saveBinderPage(SaveBinderPageRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_BINDER_PAGES +"', '" + PermissionCheck.Values.PERMISSION + "')")
    ReturnResult removePage(Long pageId);

    @Transactional(readOnly = true) BinderListResponse listBinderPages();

    @Transactional(readOnly = true) BinderListResponse binderContent(Long binderId);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    @Transactional(readOnly = true) BinderListResponse listStudyBinderPages(Long id);
}
