package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.requests.ChangeCompareRequest;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.repository.HistoryRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HistoryRepositoryImpl implements HistoryRepository {
    private static Logger logger = LoggerFactory.getLogger(HistoryRepositoryImpl.class);
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public List<RevisionData> getRevisionHistory(Long id) {
        List<RevisionData> revisions = new ArrayList<RevisionData>();
        List<RevisionEntity> entities =
                em.createQuery("SELECT r FROM RevisionEntity r " +
                        "WHERE r.key.revisionableId=:id ORDER BY r.key.revisionNo ASC", RevisionEntity.class)
                    .setParameter("id", id)
                    .getResultList();

        for(RevisionEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(entity.getData());
            if(pair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Failed to deserialize "+entity.toString());
                continue;
            }
            revisions.add(pair.getRight());
        }
        return revisions;
    }

    @Override
    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request) {
        List<RevisionEntity> entities = em.createQuery("SELECT r FROM RevisionEntity r " +
                "WHERE r.key.revisionableId = :id AND r.key.revisionNo > :begin AND r.key.revisionNo <= :end " +
                "ORDER BY r.key.revisionNo ASC", RevisionEntity.class)
                .setParameter("id", request.getId())
                .setParameter("begin", request.getBegin())
                .setParameter("end", request.getEnd())
                .getResultList();
        List<RevisionData> datas = new ArrayList<RevisionData>();
        for(RevisionEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(entity.getData());
            if(pair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Failed to deserialize "+entity.toString());
                continue;
            }
            datas.add(pair.getRight());
        }
        return datas;
    }
}
