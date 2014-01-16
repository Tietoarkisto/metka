package fi.uta.fsd.metka.mvc.search.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.model.data.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
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
            if(entity.getCurApprovedRev() != null) {
                data = entity.getCurApprovedRev().getData();
            } else {
                data = entity.getLatestRevision().getData();
            }

            RevisionData revData = metkaObjectMapper.readValue(data, RevisionData.class);
            FieldContainer field = getContainerFromRevisionData(revData, "abbreviation");
            String value = extractStringValue(field);
            if(!StringUtils.isEmpty(value)) list.add(value);
        }

        return list;
    }

    @Override
    /* If query includes no search terms then nothing is returned.
         *
         * If query includes the id then simple find by id can be done.
         * In this case the latest approved revision has to be deserialized and added to the list.
         * If no approved revision exist use latest revision.
         * Otherwise more complicated search has to be done.
         *
         * Complicated search (disregard id since it should be null):
         * Get all series revisionables
         * Get their newest approved revision
         * For each revisionable deserialize their revision data
         * Try to match search terms (abbreviation and name to revision data.
         * If it's a match then add it to list.
         * Return list
        */
    public List<RevisionData> findSeries(SeriesSearchSO query) throws IOException {
        List<RevisionData> list = new ArrayList<RevisionData>();
        if(query == null ||
                (query.getId() == null
                && StringUtils.isEmpty(query.getAbbreviation())
                && StringUtils.isEmpty(query.getName()))) {
            return list;
        }
        if(query.getId() != null) {
            // Do id based search
            SeriesEntity entity = em.find(SeriesEntity.class, query.getId());
            RevisionData data = getRevisionData(entity);
            if(data != null) list.add(data);
        } else {
            // Do complicated search
            doComplicatedSearch(query, list);
        }
        return list;
    }

    private void doComplicatedSearch(SeriesSearchSO query, List<RevisionData> list) throws IOException {
        List<SeriesEntity> entities =
                em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            RevisionData data = getRevisionData(entity);
            if(data != null) {
                if(!StringUtils.isEmpty(query.getAbbreviation())) {
                    FieldContainer field = getContainerFromRevisionData(data, "abbreviation");
                    String value = extractStringValue(field);
                    if(StringUtils.isEmpty(value) || !value.equals(query.getAbbreviation())) {
                        continue;
                    }
                }
                if(!StringUtils.isEmpty(query.getName())) {
                    FieldContainer field = getContainerFromRevisionData(data, "name");
                    String value = extractStringValue(field);
                    if(StringUtils.isEmpty(value) || !value.contains(query.getName())) {
                        continue;
                    }
                }
                list.add(data);
            }
        }
    }

    /*
    * Return revision data for the latest
    */
    @Override
    public RevisionData findSingleSeries(Integer id) throws IOException {
        SeriesEntity entity = em.find(SeriesEntity.class, id);
        if(entity == null || (entity.getLatestRevision() == null
                && entity.getCurApprovedRev() == null)) {
            // TODO: log error
            return null;
        }

        RevisionEntity revEntity = (entity.getCurApprovedRev() == null)
                ? entity.getLatestRevision() : entity.getCurApprovedRev();

        if(StringUtils.isEmpty(revEntity.getData())) {
            // TODO: log error
            return null;
        }

        RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);

        return data;
    }

    private RevisionData getRevisionData(SeriesEntity entity) throws IOException {
        if(entity != null && (entity.getCurApprovedRev() != null || entity.getLatestRevision() != null)) {
            RevisionEntity revEntity =
                    (entity.getCurApprovedRev() != null)
                            ? entity.getCurApprovedRev()
                            : entity.getLatestRevision();

            RevisionData data = metkaObjectMapper.readValue(revEntity.getData(), RevisionData.class);
            return data;
        } else return null;
    }
}
