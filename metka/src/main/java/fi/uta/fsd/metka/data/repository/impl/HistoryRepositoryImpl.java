package fi.uta.fsd.metka.data.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.repository.HistoryRepository;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.requests.ChangeCompareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/14/14
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class HistoryRepositoryImpl implements HistoryRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Override
    public List<RevisionData> getRevisionHistory(Integer id) throws IOException {
        List<RevisionData> revisions = new ArrayList<RevisionData>();
        List<RevisionEntity> entities =
                em.createQuery("SELECT r FROM RevisionEntity r " +
                        "WHERE r.key.revisionableId=:id ORDER BY r.key.revisionNo ASC", RevisionEntity.class)
                    .setParameter("id", id)
                    .getResultList();

        for(RevisionEntity entity : entities) {
            RevisionData data = metkaObjectMapper.readValue(entity.getData(), RevisionData.class);
            revisions.add(data);
        }
        return revisions;
    }

    @Override
    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request) throws IOException {
        List<RevisionEntity> entities = em.createQuery("SELECT r FROM RevisionEntity r " +
                "WHERE r.key.revisionableId = :id AND r.key.revisionNo > :begin AND r.key.revisionNo <= :end " +
                "ORDER BY r.key.revisionNo ASC", RevisionEntity.class)
                .setParameter("id", request.getId())
                .setParameter("begin", request.getBegin())
                .setParameter("end", request.getEnd())
                .getResultList();
        List<RevisionData> datas = new ArrayList<RevisionData>();
        for(RevisionEntity entity : entities) {
            datas.add(metkaObjectMapper.readValue(entity.getData(), RevisionData.class));
        }
        return datas;
    }

    @Override
    public RevisionData getRevisionByKey(Integer id, Integer revision) throws IOException {
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(entity == null) {
            return null;
        }
        RevisionData data = metkaObjectMapper.readValue(entity.getData(), RevisionData.class);
        return data;
    }
}
