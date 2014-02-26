package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.enums.repositoryResponses.RemoveResponse;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.data.RevisionData;
import javassist.NotFoundException;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

@Repository
public class GeneralRepositoryImpl implements GeneralRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;
    @Autowired
    private JSONUtil json;

    @Override
    public Integer getAdjancedRevisionableId(Integer currentId, String type, boolean forward)
            throws NotFoundException, MissingResourceException {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                    "WHERE r.id "+(forward?">":"<")+" :id AND r.type = :type " +
                    "AND r.removed = false " +
                    "AND r.curApprovedNo IS NOT NULL " +
                    "ORDER BY r.id "+(forward?"ASC":"DESC"), RevisionableEntity.class)
                .setParameter("id", currentId)
                .setParameter("type", type.toUpperCase())
                .setMaxResults(1)
                .getResultList();

        if(list.size() == 0) {
            throw new NotFoundException("");
        }
        return list.get(0).getId();
    }

    @Override
    public RemoveResponse removeDraft(String type, Integer id) {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                "WHERE r.id = :id AND r.type = :type", RevisionableEntity.class)
                .setParameter("id", id)
                .setParameter("type", type.toUpperCase())
                .getResultList();
        if(list.size() == 0) {
            return RemoveResponse.NO_REVISIONABLE;
        }
        RevisionableEntity entity = list.get(0);
        if(entity.getCurApprovedNo() != null && entity.getCurApprovedNo().equals(entity.getLatestRevisionNo())) {
            return RemoveResponse.NO_DRAFT;
        }
        RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));

        if(!rev.getState().equals(RevisionState.DRAFT)) {
            // TODO: Log error since there is data discrepancy
            return RemoveResponse.NO_DRAFT;
        }

        em.remove(rev);

        if(entity.getCurApprovedNo() == null) {
            // No revisions remaining, remove the whole revisionable entity.
            em.remove(entity);
            return RemoveResponse.FINAL_REVISION;
        } else {
            entity.setLatestRevisionNo(entity.getCurApprovedNo());
            return RemoveResponse.SUCCESS;
        }
    }

    @Override
    public RemoveResponse removeLogical(String type, Integer id) {
        List<RevisionableEntity> list = em.createQuery("SELECT r FROM RevisionableEntity r " +
                "WHERE r.id = :id AND r.type = :type", RevisionableEntity.class)
                .setParameter("id", id)
                .setParameter("type", type.toUpperCase())
                .getResultList();
        if(list.size() == 0) {
            return RemoveResponse.NO_REVISIONABLE;
        }
        RevisionableEntity entity = list.get(0);
        if(entity.getCurApprovedNo() == null) {
            return RemoveResponse.NO_APPROVED;
        }
        if(entity.getCurApprovedNo() != null && !entity.getCurApprovedNo().equals(entity.getLatestRevisionNo())) {
            return RemoveResponse.OPEN_DRAFT;
        }

        entity.setRemoved(true);
        entity.setRemovalDate(new LocalDate());
        return RemoveResponse.SUCCESS;
    }

    @Override
    public List<RevisionData> getLatestRevisionsForType(ConfigurationType type, Boolean approvedOnly) throws IOException {
        List<RevisionData> dataList = new ArrayList<>();
        List<RevisionableEntity> revisionables = em.createQuery("SELECT r FROM RevisionableEntity r", RevisionableEntity.class).getResultList();

        RevisionEntity revision;
        for(RevisionableEntity entity : revisionables) {
            revision = null;
            if(approvedOnly) {
                if(entity.getCurApprovedNo() == null) {
                    continue;
                }
                revision = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo()));
            } else {
                revision = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));
            }
            if(revision != null) {
                dataList.add(json.readRevisionDataFromString(revision.getData()));
            }
        }

        return dataList;
    }
}