package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.repository.CRUDRepository;
import fi.uta.fsd.metka.data.deprecated.StudyEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/1/13
 * Time: 9:55 AM
 */
@Repository("studyRepository")
public class StudyRepositoryImpl implements CRUDRepository<StudyEntity, String> {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public StudyEntity create(StudyEntity study) {
        em.persist(study);
        return study;
    }

    @Override
    public StudyEntity read(String id) {
        StudyEntity e = em.find(StudyEntity.class, id);

        return e;
    }

    @Override
    public StudyEntity update(StudyEntity study) {
        return em.merge(study);
    }

    @Override
    public void delete(String id) {
        em.remove(em.find(StudyEntity.class, id));
    }

    @Override
    public List<StudyEntity> listAll() {
        return em.createQuery("SELECT s FROM StudyEntity s", StudyEntity.class).getResultList();
    }
}
