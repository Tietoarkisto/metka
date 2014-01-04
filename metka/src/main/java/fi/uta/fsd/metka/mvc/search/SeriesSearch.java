package fi.uta.fsd.metka.mvc.search;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SeriesSearch {
    public List<String> findAbbreviations();
    public List<RevisionData> findSeries(SeriesSearchSO query);
}
