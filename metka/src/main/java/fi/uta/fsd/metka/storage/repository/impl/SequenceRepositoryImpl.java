package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.SequenceEntity;
import fi.uta.fsd.metka.storage.repository.SequenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {
    private static Logger logger = LoggerFactory.getLogger(SequenceRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public SequenceEntity getNewSequenceValue(String key) {
        return getNewSequenceValue(key, 1L);
    }

    @Override
    public SequenceEntity getNewSequenceValue(String key, Long initialValue) {
        SequenceEntity seq = em.find(SequenceEntity.class, key);
        if(seq == null) {
            seq = new SequenceEntity();
            seq.setKey(key);
            seq.setSequence(initialValue);
            em.persist(seq);
        } else {
            seq.setSequence(seq.getSequence()+1);
        }
        return seq;
    }
}
