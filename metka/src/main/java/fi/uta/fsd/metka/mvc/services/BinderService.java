package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderListResponse;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_BINDER_PAGES +"', 'PERMISSION')")
public interface BinderService {
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_BINDER_PAGES +"', 'PERMISSION')")
    BinderListResponse saveBinderPage(SaveBinderPageRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_BINDER_PAGES +"', 'PERMISSION')")
    ReturnResult removePage(Long pageId);

    BinderListResponse listBinderPages();

    BinderListResponse binderContent(Long binderId);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', 'PERMISSION')")
    BinderListResponse listStudyBinderPages(Long id);
}
