package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/11/13
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("revisionRepository")
public class RevisionRepositoryImpl implements CRUDRepository<RevisionEntity, RevisionKey> {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public RevisionEntity create(RevisionEntity entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public RevisionEntity read(RevisionKey id) {
        return em.find(RevisionEntity.class, id);
    }

    @Override
    public RevisionEntity update(RevisionEntity entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(RevisionKey id) {
        em.remove(read(id));
    }

    @Override
    public List<RevisionEntity> listAll() {
        return em.createQuery("SELECT r FROM RevisionEntity r").getResultList();
    }
}
