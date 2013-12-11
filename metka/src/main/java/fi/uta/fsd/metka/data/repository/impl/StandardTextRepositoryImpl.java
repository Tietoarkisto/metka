package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.deprecated.StandardTextEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 12:36 PM
 */
@Repository("standardTextRepository")
public class StandardTextRepositoryImpl implements CRUDRepository<StandardTextEntity, String> {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public StandardTextEntity create(StandardTextEntity entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public StandardTextEntity read(String id) {
        return em.find(StandardTextEntity.class, id);
    }

    @Override
    public StandardTextEntity update(StandardTextEntity entity) {
        em.merge(entity);
        return entity;
    }

    @Override
    public void delete(String id) {
        StandardTextEntity e = em.find(StandardTextEntity.class, id);
        em.remove(e);
    }

    @Override
    public List<StandardTextEntity> listAll() {
        return em.createQuery("SELECT s FROM StandardTextEntity s", StandardTextEntity.class).getResultList();
    }
}
