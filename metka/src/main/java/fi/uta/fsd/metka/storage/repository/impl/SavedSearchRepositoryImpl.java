package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.SavedExpertSearchEntity;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SavedSearchRepositoryImpl implements SavedSearchRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public List<SavedExpertSearchItem> listSavedSearches() {
        List<SavedExpertSearchItem> items = new ArrayList<>();
        List<SavedExpertSearchEntity> entities = em.createQuery("SELECT e FROM SavedExpertSearchEntity e", SavedExpertSearchEntity.class)
                .getResultList();
        for(SavedExpertSearchEntity entity : entities) {
            SavedExpertSearchItem item = new SavedExpertSearchItem();
            item.setId(entity.getId());
            item.setTitle(entity.getTitle());
            item.setQuery(entity.getQuery());
            item.setSavedAt(entity.getSavedAt().toString());
            item.setSavedBy(entity.getSavedBy());
            items.add(item);
        }
        return items;
    }

    @Override
    public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem search) {
        SavedExpertSearchEntity entity = new SavedExpertSearchEntity();
        entity.setTitle(search.getTitle());
        entity.setQuery(search.getQuery());
        entity.setSavedAt(new LocalDate());
        // TODO: Set savedBy
        em.persist(entity);

        SavedExpertSearchItem item = new SavedExpertSearchItem();
        item.setId(entity.getId());
        item.setTitle(entity.getTitle());
        item.setQuery(entity.getQuery());
        item.setSavedAt(entity.getSavedAt().toString());
        item.setSavedBy(entity.getSavedBy());

        return item;
    }

    @Override
    public Long removeExpertSearch(Long id) {
        em.remove(em.find(SavedExpertSearchEntity.class, id));
        return id;
    }
}
