package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.SavedExpertSearchEntity;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
        // This gets all saved searches
        List<SavedExpertSearchEntity> entities = em.createQuery("SELECT e FROM SavedExpertSearchEntity e", SavedExpertSearchEntity.class)
                .getResultList();

        // This gets only the searches current user has saved
        /*List<SavedExpertSearchEntity> entities = em.createQuery("SELECT e FROM SavedExpertSearchEntity e WHERE e.savedBy=:user", SavedExpertSearchEntity.class)
                .setParameter("user", AuthenticationUtil.getUserName())
                .getResultList();*/
        for(SavedExpertSearchEntity entity : entities) {
            SavedExpertSearchItem item = formSavedExpertSearchItem(entity);
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
        entity.setSavedBy(AuthenticationUtil.getUserName());
        em.persist(entity);

        SavedExpertSearchItem item = formSavedExpertSearchItem(entity);

        return item;
    }

    @Override
    public void removeExpertSearch(Long id) {
        SavedExpertSearchEntity entity = em.find(SavedExpertSearchEntity.class, id);
        em.remove(entity);
    }

    @Override
    public Pair<ReturnResult, SavedExpertSearchItem> getSavedExpertSearch(Long id) {
        SavedExpertSearchEntity entity = em.find(SavedExpertSearchEntity.class, id);
        return new ImmutablePair<>(entity == null ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, formSavedExpertSearchItem(entity));
    }

    private SavedExpertSearchItem formSavedExpertSearchItem(SavedExpertSearchEntity entity) {
        if(entity == null) {
            return null;
        }
        SavedExpertSearchItem item = new SavedExpertSearchItem();
        item.setId(entity.getId());
        item.setTitle(entity.getTitle());
        item.setQuery(entity.getQuery());
        item.setSavedAt(entity.getSavedAt().toString());
        item.setSavedBy(entity.getSavedBy());
        return item;
    }
}
