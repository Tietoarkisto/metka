package fi.uta.fsd.metka.mvc.domain.simple;

import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/3/14
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesInfo {

    private List<String> abbreviations;
    private SeriesSearchSO query;
    private List<SeriesSearchSO> results;
    private SeriesSingleSO single;

    public List<String> getAbbreviations() {
        return abbreviations;
    }

    public void setAbbreviations(List<String> abbreviations) {
        this.abbreviations = abbreviations;
    }

    public SeriesSearchSO getQuery() {
        return query;
    }

    public void setQuery(SeriesSearchSO query) {
        this.query = query;
    }

    public List<SeriesSearchSO> getResults() {
        return results;
    }

    public void setResults(List<SeriesSearchSO> results) {
        this.results = results;
    }

    public SeriesSingleSO getSingle() {
        return single;
    }

    public void setSingle(SeriesSingleSO single) {
        this.single = single;
    }
}
