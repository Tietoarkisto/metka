package fi.uta.fsd.metka.mvc.search.impl;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("seriesSearch")
public class SlowSeriesSearchImpl implements SeriesSearch {
    @Override
    public List<String> findAbbreviations() {
        List<String> list = new ArrayList<String>();
        /* TODO:
         * Get all series revisionables.
         * Get their newest approved revision.
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
