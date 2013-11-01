package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.repository.MaterialRepository;
import fi.uta.fsd.metka.data.entity.MaterialEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 9:55 AM
 */
@Repository("materialRepository")
public class MaterialRepositoryImpl implements MaterialRepository {
    @PersistenceContext(name = "entityManager")
    EntityManager em;

    @Override
    public MaterialEntity update(MaterialEntity material) {
        return em.merge(material);
    }

    @Override
    public Long create(MaterialEntity material) {
        em.persist(material);
        return material.getId();
    }

    @Override
    public void delete(MaterialEntity material) {
        em.remove(material);
    }

    @Override
    public List<MaterialEntity> findAll() {
        return em.createQuery("SELECT m FROM MaterialEntity m", MaterialEntity.class).getResultList();
    }

    @Override
    public MaterialEntity findById(Long id) {
        Query q = em.createQuery("SELECT m FROM MaterialEntity m WHERE m.id = :id", MaterialEntity.class);
        q.setParameter("id", id);
        return (MaterialEntity)q.getSingleResult();
    }
}
