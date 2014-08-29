package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.transfer.expert.ExpertSearchListResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', 'PERMISSION')")
public interface ExpertSearchService {
    ExpertSearchQueryResponse performQuery(ExpertSearchQueryRequest request);

    ExpertSearchListResponse listSavedSearcher();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_SAVE_EXPERT_SEARCH +"', 'PERMISSION')")
    SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem item);

    @PreAuthorize("hasPermission(#id, 'REMOVE_SEARCH')")
    void removeExpertSearch(Long id);
}
