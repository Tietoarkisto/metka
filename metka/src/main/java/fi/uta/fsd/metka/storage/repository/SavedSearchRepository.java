package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SavedSearchRepository {
    @Transactional(readOnly = true) public List<SavedExpertSearchItem> listSavedSearches();
    public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem search);
    public void removeExpertSearch(Long id);
    public Pair<ReturnResult, SavedExpertSearchItem> getSavedExpertSearch(Long id);
}
