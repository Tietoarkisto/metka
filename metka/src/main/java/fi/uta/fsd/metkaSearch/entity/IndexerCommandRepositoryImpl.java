package fi.uta.fsd.metkaSearch.entity;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class IndexerCommandRepositoryImpl implements IndexerCommandRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public void addIndexerCommand(IndexerCommand command) {
        IndexerCommandEntity entity = IndexerCommandEntity.buildFromCommand(command);
        em.persist(entity);
    }

    @Override
    public void markCommandAsHandled(Long id) {
        IndexerCommandEntity entity = em.find(IndexerCommandEntity.class, id);
        if(entity != null) {
            entity.setHandled(new LocalDateTime());
        }
    }

    @Override
    public IndexerCommand getNextCommand(IndexerConfigurationType type, String path) {
        IndexerCommandEntity entity = null;
        List<IndexerCommandEntity> entities = em.createQuery("SELECT e FROM IndexerCommandEntity e " +
                "WHERE e.requested IS NULL AND e.type=:type AND e.path=:path ORDER BY e.created ASC", IndexerCommandEntity.class)
                .setParameter("type", type)
                .setParameter("path", path)
                .setMaxResults(1)
                .getResultList();
        if(entities.size() == 1) {
            entity = entities.get(0);
        }
        if(entity != null) {
            entity.setRequested(new LocalDateTime());
            return entity.buildCommandFromEntity();
        } else {
            return null;
        }
    }

    @Override
    public IndexerCommand getNextCommandWithoutChange() {
        IndexerCommandEntity entity = null;
        List<IndexerCommandEntity> entities = em.createQuery("SELECT e FROM IndexerCommandEntity e " +
                "WHERE e.requested IS NULL ORDER BY e.created ASC", IndexerCommandEntity.class)
                .setMaxResults(1)
                .getResultList();
        if(entities.size() == 1) {
            entity = entities.get(0);
        }
        if(entity != null) {
            return entity.buildCommandFromEntity();
        } else {
            return null;
        }
    }

    @Override
    public void clearAllRequests() {
        em.createQuery("UPDATE IndexerCommandEntity e SET e.requested=NULL WHERE e.handled IS NULL").executeUpdate();
    }

    @Override
    public void removeAllHandled() {
        em.createQuery("DELETE FROM IndexerCommandEntity e WHERE e.handled IS NOT NULL").executeUpdate();
    }
}
