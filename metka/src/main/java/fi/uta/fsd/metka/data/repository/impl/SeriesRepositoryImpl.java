package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.SeriesEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/15/13
 * Time: 1:14 PM
 */
@Repository("seriesRepository")
public class SeriesRepositoryImpl implements CRUDRepository<SeriesEntity, Integer> {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public SeriesEntity create(SeriesEntity entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    @Override
    public SeriesEntity read(Integer id) {
        return em.find(SeriesEntity.class, id);
    }

    @Override
    public SeriesEntity update(SeriesEntity entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(Integer id) {
        SeriesEntity series = em.find(SeriesEntity.class, id);
        em.remove(series);
    }

    @Override
    public List<SeriesEntity> listAll() {
        return em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
    }
}
