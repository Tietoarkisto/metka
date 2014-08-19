package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.SequenceEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GeneralRepositoryImpl implements GeneralRepository {
    private static Logger logger = LoggerFactory.getLogger(GeneralRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public Pair<ReturnResult, RevisionableInfo> getRevisionableInfo(Long id) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        if(entity == null) {
            // No entity found, can't return any info
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        RevisionableInfo info = new RevisionableInfo(entity.getId(), ConfigurationType.fromValue(entity.getType()),
                entity.getCurApprovedNo(), entity.getLatestRevisionNo(),
                entity.getRemoved(), entity.getRemovalDate(), entity.getRemovedBy());
        return new ImmutablePair<>(ReturnResult.REVISIONABLE_FOUND, info);
    }

    @Override
    public Pair<ReturnResult, Long> getAdjacentRevisionableId(Long currentId, String type, boolean forward) {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                    "WHERE r.id "+(forward?">":"<")+" :id AND r.type = :type " +
                    "AND r.removed = false " +
                    "AND r.curApprovedNo IS NOT NULL " +
                    "ORDER BY r.id "+(forward?"ASC":"DESC"), RevisionableEntity.class)
                .setParameter("id", currentId)
                .setParameter("type", ConfigurationType.valueOf(type.toUpperCase()))
                .setMaxResults(1)
                .getResultList();

        if(list.size() == 0) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        return new ImmutablePair<>(ReturnResult.REVISIONABLE_FOUND, list.get(0).getId());
    }

    @Override
    public DraftRemoveResponse removeDraft(String type, Long id) {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                "WHERE r.id = :id AND r.type = :type", RevisionableEntity.class)
                .setParameter("id", id)
                .setParameter("type", ConfigurationType.valueOf(type.toUpperCase()))
                .getResultList();
        if(list.size() == 0) {
            return new DraftRemoveResponse(DraftRemoveResponse.Response.NO_REVISIONABLE, id, null);
        }
        RevisionableEntity entity = list.get(0);
        if(!entity.hasDraft()) {
            return new DraftRemoveResponse(DraftRemoveResponse.Response.NO_DRAFT, id, null);
        }
        Integer no = entity.getLatestRevisionNo();
        RevisionEntity rev = em.find(RevisionEntity.class, entity.latestRevisionKey());

        if(!rev.getState().equals(RevisionState.DRAFT)) {
            // TODO: Log error since there is data discrepancy
            return new DraftRemoveResponse(DraftRemoveResponse.Response.NO_DRAFT, id, no);
        }

        em.remove(rev);

        if(entity.getCurApprovedNo() == null) {
            // No revisions remaining, remove the whole revisionable entity.

            em.remove(entity);
            return new DraftRemoveResponse(DraftRemoveResponse.Response.FINAL_REVISION, id, no);
        } else {

            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            return new DraftRemoveResponse(DraftRemoveResponse.Response.SUCCESS, id, no);
        }
    }

    @Override
    public LogicalRemoveResponse removeLogical(String type, Long id) {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                "WHERE r.id = :id AND r.type = :type", RevisionableEntity.class)
                .setParameter("id", id)
                .setParameter("type", type.toUpperCase())
                .getResultList();
        if(list.size() == 0) {
            return LogicalRemoveResponse.NO_REVISIONABLE;
        }
        RevisionableEntity entity = list.get(0);
        if(entity.getCurApprovedNo() == null) {
            return LogicalRemoveResponse.NO_APPROVED;
        }
        if(entity.hasDraft()) {
            return LogicalRemoveResponse.OPEN_DRAFT;
        }

        entity.setRemoved(true);
        entity.setRemovalDate(new LocalDateTime());
        return LogicalRemoveResponse.SUCCESS;
    }

    @Override
    public Pair<ReturnResult, RevisionData> getLatestRevisionForIdAndType(Long id, boolean approvedOnly, ConfigurationType type) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        if(entity == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        if(type != null && !entity.getType().equals(type.toValue())) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_OF_INCORRECT_TYPE, null);
        }
        if(entity.getLatestRevisionNo() == null) {
            // This is a serious error
            return new ImmutablePair<>(ReturnResult.NO_REVISION_FOR_REVISIONABLE, null);
        }
        if(approvedOnly && entity.getCurApprovedNo() == null) {
            // This is not a serious problem since approved revision
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_FOUND, null);
        }
        Pair<ReturnResult, RevisionData> pair = getRevisionDataOfType((approvedOnly) ? entity.currentApprovedRevisionKey() : entity.latestRevisionKey(), type);
        if(pair.getLeft() == ReturnResult.REVISION_NOT_FOUND) {
            // Since we know that the revision should exist upgrade the error to NO_REVISION_FOR_REVISIONABLE
            logger.error("Revision that should exist "+((approvedOnly) ? entity.currentApprovedRevisionKey() : entity.latestRevisionKey())+" was not found for entity "+entity.toString());
            return new ImmutablePair<>(ReturnResult.NO_REVISION_FOR_REVISIONABLE, null);
        }
        return pair;
    }

    @Override
    public Pair<ReturnResult, Integer> getLatestRevisionNoForIdAndType(Long id, boolean approvedOnly, ConfigurationType type) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        if(entity == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        if(type != null && !entity.getType().equals(type.toValue())) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_OF_INCORRECT_TYPE, null);
        }
        if(approvedOnly && entity.getCurApprovedNo() == null) {
            return new ImmutablePair<>(ReturnResult.NO_REVISION_FOR_REVISIONABLE, null);
        }
        return new ImmutablePair<>(ReturnResult.REVISION_FOUND, (approvedOnly) ? entity.getCurApprovedNo() : entity.getLatestRevisionNo());
    }

    @Override
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer no) {
        return getRevisionDataOfType(new RevisionKey(id, no), null);
    }

    @Override
    public Pair<ReturnResult, RevisionData> getRevisionDataOfType(Long id, Integer no, ConfigurationType type) {
        return getRevisionDataOfType(new RevisionKey(id, no), type);
    }

    @Override
    public Pair<ReturnResult, RevisionData> getRevisionDataOfType(RevisionKey key, ConfigurationType type) {
        RevisionEntity entity = em.find(RevisionEntity.class, key);
        if(entity == null) {
            // Didn't found entity
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_FOUND, null);
        }
        Pair<ReturnResult, RevisionData> pair = getRevisionDataFromEntity(entity);
        // Sanity check
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Couldn't get revision data from "+entity.toString());
            return pair;
        }
        if(type != null && pair.getRight().getConfiguration().getType() != type) {
            // Requested revision isn't a study variables revision
            logger.info("Someone requested a revision of type " + type + " with id " + key.getRevisionableId() + " and no " + key.getRevisionNo() + ". " +
                    "These do not match a revision of type " + type + " but instead a " + pair.getRight().getConfiguration().getType());
            return new ImmutablePair<>(ReturnResult.REVISION_OF_INCORRECT_TYPE, null);
        }
        return pair;
    }

    private Pair<ReturnResult, RevisionData> getRevisionDataFromEntity(RevisionEntity revision) {
        // Sanity check
        if(!StringUtils.hasText(revision.getData())) {
            logger.error(revision.toString()+" was found with no data.");
            return new ImmutablePair<>(ReturnResult.REVISION_CONTAINED_NO_DATA, null);
        }
        Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(revision.getData());
        if(pair.getLeft() == ReturnResult.DESERIALIZATION_SUCCESS) {
            pair = new ImmutablePair<>(ReturnResult.REVISION_FOUND, pair.getRight());
        }
        return pair;
    }

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

    @Override
    public List<Integer> getAllRevisionNumbers(Long id) {
        List<Integer> numbers = new ArrayList<>();
        List<RevisionEntity> revisions = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId=:id", RevisionEntity.class)
                .setParameter("id", id)
                .getResultList();
        for(RevisionEntity revision : revisions) {
            numbers.add(revision.getKey().getRevisionNo());
        }
        return numbers;
    }

    @Override
    public ReturnResult updateRevisionData(RevisionData revision) {
        Pair<ReturnResult, String> string = json.serialize(revision);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            logger.error("Failed at serializing "+revision.toString());
            return string.getLeft();
        }
        RevisionEntity entity = em.find(RevisionEntity.class, RevisionKey.fromModelKey(revision.getKey()));
        if(entity == null) {
            return ReturnResult.REVISION_NOT_FOUND;
        }
        entity.setData(string.getRight());
        em.merge(entity);
        return ReturnResult.REVISION_UPDATE_SUCCESSFUL;
    }

    @Override
    public Pair<ReturnResult, RevisionKey> createNewRevision(RevisionData data) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, data.getKey().getId());
        if(entity == null) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }
        if(entity.getLatestRevisionNo() == null) {
            return new ImmutablePair<>(ReturnResult.NO_REVISION_FOR_REVISIONABLE, null);
        }
        // Let's just make this check
        if(!entity.getLatestRevisionNo().equals(entity.getCurApprovedNo())) {
            return new ImmutablePair<>(ReturnResult.REVISION_FOUND, null);
        }
        RevisionEntity revision = new RevisionEntity(new RevisionKey(entity.getId(), entity.getLatestRevisionNo()+1));
        revision.setState(RevisionState.DRAFT);
        em.persist(revision);
        return new ImmutablePair<>(ReturnResult.REVISION_CREATED, revision.getKey());
    }
}
