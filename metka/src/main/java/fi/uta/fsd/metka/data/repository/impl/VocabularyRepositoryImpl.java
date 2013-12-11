package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.deprecated.VocabularyEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 12:35 PM
 */
@Repository("vocabularyRepository")
public class VocabularyRepositoryImpl implements CRUDRepository<VocabularyEntity, String> {
    @PersistenceContext(name = "entityManager")
    EntityManager em;

    @Override
    public VocabularyEntity create(VocabularyEntity entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public VocabularyEntity read(String id) {
        VocabularyEntity e = em.find(VocabularyEntity.class, id);
        return e;
    }

    @Override
    public VocabularyEntity update(VocabularyEntity entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(String id) {
        VocabularyEntity e = em.find(VocabularyEntity.class, id);
        em.remove(e);
    }

    @Override
    public List<VocabularyEntity> listAll() {
        return em.createQuery("SELECT v FROM VocabularyEntity v", VocabularyEntity.class).getResultList();
    }
}