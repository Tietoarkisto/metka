package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRemoveRepository;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Performs a removal to given transfer data if possible
 *
 * Valid results are:
 *      SUCCESS_DRAFT - Draft was removed successfully, user should be moved to latest approved revision
 *      SUCCESS_LOGICAL - Logical removal was performed successfully, new up to date data should be loaded for the revision
 *      FINAL_REVISION - Draft was removed but no further revisions existed for the revisionable so the revisionable was removed also.
 * All other return values are errors
 */
@Repository
public class RevisionRemoveRepositoryImpl implements RevisionRemoveRepository {
    private static final Logger logger = LoggerFactory.getLogger(RevisionRemoveRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public RemoveResult remove(TransferData transferData) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(RevisionKey.fromModelKey(transferData.getKey()));
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return RemoveResult.NOT_FOUND;
        }

        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return removeDraft(pair.getRight());
        } else {
            return removeLogical(pair.getRight());
        }
    }

    private RemoveResult removeDraft(RevisionData data) {
        if(!data.getHandler().equals(AuthenticationUtil.getUserName())) {
            logger.error("User "+AuthenticationUtil.getUserName()+" tried to remove draft belonging to "+data.getHandler());
            return RemoveResult.WRONG_USER;
        }

        em.remove(em.find(RevisionEntity.class, RevisionKey.fromModelKey(data.getKey())));

        List<RevisionEntity> entities = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId=:id", RevisionEntity.class)
                .setParameter("id", data.getKey().getId())
                .getResultList();

        if(entities.isEmpty()) {
            em.remove(em.find(RevisionableEntity.class, data.getKey().getId()));
            return RemoveResult.FINAL_REVISION;
        } else {
            RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            return RemoveResult.SUCCESS_DRAFT;
        }
    }

    private RemoveResult removeLogical(RevisionData data) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(data.getKey().getId(), false, data.getConfiguration().getType());

        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // This should never happen since we found the revision data provided for this method
            return RemoveResult.NOT_FOUND;
        }
        if(pair.getRight().getState() == RevisionState.DRAFT) {
            return RemoveResult.OPEN_DRAFT;
        }

        RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
        entity.setRemoved(true);
        entity.setRemovalDate(new LocalDateTime());
        entity.setRemovedBy(AuthenticationUtil.getUserName());
        return RemoveResult.SUCCESS_LOGICAL;
    }
}
