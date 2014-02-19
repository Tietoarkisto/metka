package fi.uta.fsd.metka.mvc.search.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

@Repository("generalSearch")
public class SlowGeneralSearchImpl implements GeneralSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ObjectMapper metkaObjectMapper;

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

        RevisionData data = metkaObjectMapper.readValue(entity.getData(), RevisionData.class);
        if(data.getConfiguration().getType() != type) {
            return null;
        }

        return data;
    }
}
