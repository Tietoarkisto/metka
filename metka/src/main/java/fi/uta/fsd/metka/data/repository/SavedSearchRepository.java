package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;

import java.util.List;

public interface SavedSearchRepository {
    public List<SavedExpertSearchItem> listSavedSearches();
    public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem search);
    public Long removeExpertSearch(Long id);
}
