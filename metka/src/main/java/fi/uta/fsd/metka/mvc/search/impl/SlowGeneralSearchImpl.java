package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository("generalSearch")
public class SlowGeneralSearchImpl implements GeneralSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public Integer findSingleRevisionNo(Integer id) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        if(entity == null || (entity.getLatestRevisionNo() == null && entity.getCurApprovedNo() == null)) {
            // TODO: log error
            return null;
        }

        Integer revision = (entity.getCurApprovedNo() == null)?entity.getLatestRevisionNo():entity.getCurApprovedNo();
        return revision;
    }

    @Override
    public RevisionData findSingleRevision(Integer id, Integer revision, ConfigurationType type) throws IOException {
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(entity == null) {
            return null;
        }

        if(StringUtils.isEmpty(entity.getData())) {
            // TODO: log error
            return null;
        }

        RevisionData data = json.readRevisionDataFromString(entity.getData());
        if(data.getConfiguration().getType() != type) {
            return null;
        }

        return data;
    }

    @Override
    public List<RevisionDataRemovedContainer> tempFindAllStudies() throws IOException {
        List<RevisionDataRemovedContainer> result = new ArrayList<>();
        List<StudyEntity> entities = formFindQuery().getResultList();
        RevisionDataRemovedContainer container;
        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(entity.getCurApprovedNo() != null) {
                    RevisionEntity rev = em.find(RevisionEntity.class, entity.currentApprovedRevisionKey());
                    RevisionData data = json.readRevisionDataFromString(rev.getData());

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
                if(entity.hasDraft()) {
                    RevisionEntity rev = em.find(RevisionEntity.class, entity.latestRevisionKey());
                    RevisionData data = json.readRevisionDataFromString(rev.getData());

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
            } else {
                RevisionEntity rev = em.find(RevisionEntity.class, entity.getLatestRevisionNo());
                RevisionData data = json.readRevisionDataFromString(rev.getData());

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
