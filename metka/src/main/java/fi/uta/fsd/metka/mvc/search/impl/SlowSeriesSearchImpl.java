package fi.uta.fsd.metka.mvc.search.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.impl.SeriesEntity;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

        List<SeriesEntity> entities = em.createQuery("SELECT s FROM SeriesEntity s", SeriesEntity.class).getResultList();
        for(SeriesEntity entity : entities) {
            String data = null;
            if(entity.getCurApprovedRev() == null) {
                data = entity.getCurApprovedRev().getData();
            } else {
                data = entity.getLatestRevision().getData();
            }

            RevisionData revData = metkaObjectMapper.readValue(data, RevisionData.class);
            //FormContainer form = getContainerFromRevisionData(revData, "abbreviation");
        }
        /* TODO:
         * Get all series revisionables.
         * Get their newest approved revision or if none then their latest revision.
         * For each revisionable deserialize their revision data,
         * get the abbreaviation field and add its value to the set if it is not yet there.
         * Return list
         *
        */
        return list;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RevisionData> findSeries(SeriesSearchSO query) {
        List<RevisionData> list = new ArrayList<RevisionData>();
        /* TODO:
         * If query includes the id then simple find by id can be done.
         * If query includes no search terms then simple list all is sufficient.
         * In both cases the latest approved revision has to be deserialized and added to the list.
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
        return list;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
