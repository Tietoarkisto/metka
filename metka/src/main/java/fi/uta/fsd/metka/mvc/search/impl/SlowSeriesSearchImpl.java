package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.util.JSONUtil;
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
import static fi.uta.fsd.metka.data.util.ConversionUtil.*;

@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Override
    public List<String> findAbbreviations() throws IOException {
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

            RevisionData revData = json.readRevisionDataFromString(data);
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
        Integer seriesno = stringToInteger(query.getByKey("seriesno"));
        if(query != null && (seriesno != null || !query.isSearchRemoved())) {
            qry += " WHERE ";
            if(seriesno != null) {
                qry += "r.id = :id";
            }
            if(seriesno != null && !query.isSearchRemoved()) {
                qry += " AND ";
            }
            if(!query.isSearchRemoved()) {
                qry += "r.removed = false";
            }
        }
        qry += " ORDER BY r.id ASC";
        TypedQuery<RevisionableEntity> typedQuery = em.createQuery(qry, RevisionableEntity.class);
        if(query != null && seriesno != null) {
            typedQuery.setParameter("id", seriesno);
        }
        return typedQuery;
    }

    private RevisionData checkSearch(RevisionEntity revision, SeriesSearchSO query) throws IOException {

        RevisionData data = json.readRevisionDataFromString(revision.getData());
        if(!StringUtils.isEmpty(query.getByKey("seriesabb"))) {
            ValueFieldContainer field = getValueFieldContainerFromRevisionData(data, "seriesabb");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().equals(((String)query.getByKey("seriesabb")).toUpperCase())) {
                return null;
            }
        }
        if(!StringUtils.isEmpty(query.getByKey("seriesname"))) {
            ValueFieldContainer field = getValueFieldContainerFromRevisionData(data, "seriesname");
            String value = extractStringSimpleValue(field);
            if(StringUtils.isEmpty(value) || !value.toUpperCase().contains(((String)query.getByKey("seriesname")).toUpperCase())) {
                return null;
            }
        }
        return data;
    }
}
