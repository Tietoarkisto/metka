package fi.uta.fsd.metka.mvc.search.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Override
    public List<String> findAbbreviations() throws JsonParseException, JsonMappingException, IOException {
        List<String> list = new ArrayList<String>();
        list.add("");

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            String data = null;
            if(entity.getCurApprovedNo() != null) {
                //data = entity.getCurApprovedRev().getData();
                data = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo())).getData();
            } else {
                //data = entity.getLatestRevision().getData();
                data = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo())).getData();
            }

            RevisionData revData = metkaObjectMapper.readValue(data, RevisionData.class);
            FieldContainer field = getContainerFromRevisionData(revData, "abbreviation");
            String value = extractStringSimpleValue(field);
            if(!StringUtils.isEmpty(value)) list.add(value);
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List findSeries(SeriesSearchSO query) throws IOException {
        List result = new ArrayList();
        List<RevisionableEntity> entities = formFindQuery(query).getResultList();

        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(query.isSearchApproved() && entity.getCurApprovedNo() != null) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        Object[] revision = new Object[2];
                        revision[0] = data;
                        revision[1] = false;
                        result.add(revision);
                    }
                }
                if(query.isSearchDraft() && !entity.getLatestRevisionNo().equals(entity.getCurApprovedNo())) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        Object[] revision = new Object[2];
                        revision[0] = data;
                        revision[1] = false;
                        result.add(revision);
                    }
                }
            } else {
                if(query.isSearchRemoved()) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        Object[] revision = new Object[2];
                        revision[0] = data;
                        revision[1] = true;
                        result.add(revision);
                    }
                }
            }
        }
        return result;
    }

    private TypedQuery<RevisionableEntity> formFindQuery(SeriesSearchSO query) {
        String qry = "SELECT r FROM RevisionableEntity r";
        if(query != null && (query.getId() != null || !query.isSearchRemoved())) {
            qry += " WHERE ";
            if(query.getId() != null) {
                qry += "r.id = :id";
            }
            if(query.getId() != null && !query.isSearchRemoved()) {
                qry += " AND ";
            }
            if(!query.isSearchRemoved()) {
                qry += "r.removed = false";
            }
        }
        qry += " ORDER BY r.id ASC";
        TypedQuery<RevisionableEntity> typedQuery = em.createQuery(qry, RevisionableEntity.class);
        if(query != null && query.getId() != null) {
            typedQuery.setParameter("id", query.getId());
        }
        return typedQuery;
    }

    private RevisionData checkSearch(RevisionEntity revision, SeriesSearchSO query) throws IOException {

        RevisionData data = metkaObjectMapper.readValue(revision.getData(), RevisionData.class);
        if(!StringUtils.isEmpty(query.getAbbreviation())) {
            FieldContainer field = getContainerFromRevisionData(data, "abbreviation");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().equals(query.getAbbreviation().toUpperCase())) {
                return null;
            }
        }
        if(!StringUtils.isEmpty(query.getName())) {
            FieldContainer field = getContainerFromRevisionData(data, "name");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().contains(query.getName().toUpperCase())) {
                return null;
            }
        }
        return data;
    }

    /*
    * Return revision data for the latest
    */
    @Override
    public Integer findSingleSeriesRevisionNo(Integer id) {
        SeriesEntity entity = em.find(SeriesEntity.class, id);
        if(entity == null || (entity.getLatestRevisionNo() == null && entity.getCurApprovedNo() == null)) {
            // TODO: log error
            return null;
        }

        /*RevisionEntity revEntity = (entity.getCurApprovedNo() == null)
                ? entity.getLatestRevision() : entity.getCurApprovedRev();*/
        Integer revision = (entity.getCurApprovedNo() == null)?entity.getLatestRevisionNo():entity.getCurApprovedNo();
        return revision;
    }

    @Override
    public RevisionData findSingleRevision(Integer id, Integer revision) throws IOException {
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(entity == null) {
            return null;
        }

        if(StringUtils.isEmpty(entity.getData())) {
            // TODO: log error
            return null;
        }

        RevisionData data = metkaObjectMapper.readValue(entity.getData(), RevisionData.class);
        if(data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        return data;
    }

    private RevisionData getRevisionData(SeriesEntity entity) throws IOException {
        if(entity != null && (entity.getCurApprovedNo() != null || entity.getLatestRevisionNo() != null)) {
            /*RevisionEntity revEntity =
                    (entity.getCurApprovedRev() != null)
                            ? entity.getCurApprovedRev()
                            : entity.getLatestRevision();*/
            RevisionEntity revEntity = (entity.getCurApprovedNo() == null)
                    ? em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()))
                    : em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo()));

            RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);
            return data;
        } else return null;
    }
}
