package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/16/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("revisionableRepository")
public class RevisionableRepositoryImpl implements CRUDRepository<RevisionableEntity, Integer> {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public RevisionableEntity create(RevisionableEntity entity)  {
        em.persist(entity);
        return entity;
    }

    @Override
    public RevisionableEntity read(Integer id) {
        return em.find(RevisionableEntity.class, id);
    }

    @Override
    public RevisionableEntity update(RevisionableEntity entity) {
        em.merge(entity);
        return entity;
    }

    @Override
    public void delete(Integer id) {
        em.remove(em.find(RevisionableEntity.class, id));
    }

    @Override
    public List<RevisionableEntity> listAll() {
        return em.createQuery("SELECT r FROM RevisionableEntity r").getResultList();
    }
}
