package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.transfer.expert.ExpertSearchListResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional
public interface ExpertSearchService {
    @Transactional(readOnly = true) ExpertSearchQueryResponse performQuery(ExpertSearchQueryRequest request);

    @Transactional(readOnly = true) ExpertSearchListResponse listSavedSearcher();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_SAVE_EXPERT_SEARCH +"', '" + PermissionCheck.Values.PERMISSION + "')")
    SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem item);

    @PreAuthorize("hasPermission(#id, '" + PermissionCheck.Values.REMOVE_SEARCH + "')")
    void removeExpertSearch(Long id);
}
