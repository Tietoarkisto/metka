package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository("generalSearch")
public class SlowGeneralSearchImpl implements GeneralSearch {
    private static Logger logger = LoggerFactory.getLogger(SlowGeneralSearchImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public Integer findSingleRevisionNo(Long id) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        if(entity == null) {
            logger.info("User tried to find a single revision for revisionable ("+id+") but no revisionable could be found.");
            return null;
        }
        if(entity.getLatestRevisionNo() == null && entity.getCurApprovedNo() == null) {
            logger.error("Revisionable ("+id+") has no latest revision number or current approved revision number.");
            return null;
        }

        Integer revision = (entity.getCurApprovedNo() == null)?entity.getLatestRevisionNo():entity.getCurApprovedNo();
        return revision;
    }

    @Override
    public RevisionData findSingleRevision(Long id, Integer revision, ConfigurationType type) {
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(entity == null) {
            return null;
        }

        if(StringUtils.isEmpty(entity.getData())) {
            logger.error("Revision ("+id+"|"+revision+") doesn't have data.");
            return null;
        }

        RevisionData data = json.deserializeRevisionData(entity.getData());
        if(data == null || data.getConfiguration().getType() != type) {
            return null;
        }

        return data;
    }

    @Override
    public List<RevisionDataRemovedContainer> tempFindAllStudies() {
        List<RevisionDataRemovedContainer> result = new ArrayList<>();
        List<StudyEntity> entities = formFindQuery().getResultList();
        RevisionDataRemovedContainer container;
        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(entity.getCurApprovedNo() != null) {
                    RevisionEntity rev = em.find(RevisionEntity.class, entity.currentApprovedRevisionKey());
                    RevisionData data = json.deserializeRevisionData(rev.getData());

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
                if(entity.hasDraft()) {
                    RevisionEntity rev = em.find(RevisionEntity.class, entity.latestRevisionKey());
                    RevisionData data = json.deserializeRevisionData(rev.getData());

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
            } else {
                RevisionEntity rev = em.find(RevisionEntity.class, entity.getLatestRevisionNo());
                RevisionData data = json.deserializeRevisionData(rev.getData());

                if(data != null) {
                    container = new RevisionDataRemovedContainer(data, true);
                    result.add(container);
                }
            }
        }
        return result;
    }

    private TypedQuery<StudyEntity> formFindQuery() {
        String qry = "SELECT r FROM StudyEntity r";

        qry += " ORDER BY r.id ASC";
        TypedQuery<StudyEntity> typedQuery = em.createQuery(qry, StudyEntity.class);

        return typedQuery;
    }
}
