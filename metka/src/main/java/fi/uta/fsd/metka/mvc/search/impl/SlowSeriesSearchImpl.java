package fi.uta.fsd.metka.mvc.search.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
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
            ValueFieldContainer field = getValueFieldContainerFromRevisionData(revData, "seriesabb");
            String value = extractStringSimpleValue(field);
            if(!StringUtils.isEmpty(value)) list.add(value);
        }
        Collections.sort(list);
        return list;
    }

    @Override
    public List<RevisionDataRemovedContainer> findSeries(SeriesSearchSO query) throws IOException {
        List<RevisionDataRemovedContainer> result = new ArrayList<>();
        List<RevisionableEntity> entities = formFindQuery(query).getResultList();
        RevisionDataRemovedContainer container;
        for(RevisionableEntity entity : entities) {
            if(!entity.getRemoved()) {
                if(query.isSearchApproved() && entity.getCurApprovedNo() != null) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
                if(query.isSearchDraft() && !entity.getLatestRevisionNo().equals(entity.getCurApprovedNo())) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, false);
                        result.add(container);
                    }
                }
            } else {
                if(query.isSearchRemoved()) {
                    RevisionEntity rev = em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()));
                    RevisionData data = checkSearch(rev, query);

                    if(data != null) {
                        container = new RevisionDataRemovedContainer(data, true);
                        result.add(container);
                    }
                }
            }
        }
        return result;
    }

    private TypedQuery<RevisionableEntity> formFindQuery(SeriesSearchSO query) {
        String qry = "SELECT r FROM RevisionableEntity r";
        if(query != null && (query.getSeriesno() != null || !query.isSearchRemoved())) {
            qry += " WHERE ";
            if(query.getSeriesno() != null) {
                qry += "r.id = :id";
            }
            if(query.getSeriesno() != null && !query.isSearchRemoved()) {
                qry += " AND ";
            }
            if(!query.isSearchRemoved()) {
                qry += "r.removed = false";
            }
        }
        qry += " ORDER BY r.id ASC";
        TypedQuery<RevisionableEntity> typedQuery = em.createQuery(qry, RevisionableEntity.class);
        if(query != null && query.getSeriesno() != null) {
            typedQuery.setParameter("id", query.getSeriesno());
        }
        return typedQuery;
    }

    private RevisionData checkSearch(RevisionEntity revision, SeriesSearchSO query) throws IOException {

        RevisionData data = metkaObjectMapper.readValue(revision.getData(), RevisionData.class);
        if(!StringUtils.isEmpty(query.getSeriesabb())) {
            ValueFieldContainer field = getValueFieldContainerFromRevisionData(data, "seriesabb");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().equals(query.getSeriesabb().toUpperCase())) {
                return null;
            }
        }
        if(!StringUtils.isEmpty(query.getSeriesname())) {
            ValueFieldContainer field = getValueFieldContainerFromRevisionData(data, "seriesname");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().contains(query.getSeriesname().toUpperCase())) {
                return null;
            }
        }
        return data;
    }

    private RevisionData getRevisionData(SeriesEntity entity) throws IOException {
        if(entity != null && (entity.getCurApprovedNo() != null || entity.getLatestRevisionNo() != null)) {
            RevisionEntity revEntity = (entity.getCurApprovedNo() == null)
                    ? em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getLatestRevisionNo()))
                    : em.find(RevisionEntity.class, new RevisionKey(entity.getId(), entity.getCurApprovedNo()));

            RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);
            return data;
        } else return null;
    }
}
