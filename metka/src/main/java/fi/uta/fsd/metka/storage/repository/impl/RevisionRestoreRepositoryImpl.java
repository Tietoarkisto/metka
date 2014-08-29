package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRestoreRepository;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Restores removed revisionables back to usage.
 * Needs special permissions to use but those are checked on the interface
 * Successful result is SUCCESS_RESTORE, all other results are failures of some sort
 */
@Repository
public class RevisionRestoreRepositoryImpl implements RevisionRestoreRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public RemoveResult restore(TransferData transferData) {
        Pair<ReturnResult, RevisionableInfo> pair = revisions.getRevisionableInfo(transferData.getKey().getId());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }
        if(!pair.getRight().getRemoved()) {
            return RemoveResult.NOT_REMOVED;
        }
        RevisionableEntity entity = em.find(RevisionableEntity.class, transferData.getKey().getId());
        entity.setRemoved(false);
        entity.setRemovedBy(null);
        entity.setRemovalDate(null);
        return RemoveResult.SUCCESS_RESTORE;
    }
}
