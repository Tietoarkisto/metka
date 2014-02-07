package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.model.configuration.Configuration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/3/14
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudyInfo {
    private StudySearchSO query;
    private StudySingleSO single;
    private List<StudySearchResultSO> results;
    private Configuration configuration;
    private List<SeriesReference> series;
    private List<ErroneousStudy> erroneous;

    public StudySearchSO getQuery() {
        return query;
    }

    public void setQuery(StudySearchSO query) {
        this.query = query;
    }

    public StudySingleSO getSingle() {
        return single;
    }

    public void setSingle(StudySingleSO single) {
        this.single = single;
    }

    public List<StudySearchResultSO> getResults() {
        return results;
    }

    public void setResults(List<StudySearchResultSO> results) {
        this.results = results;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<SeriesReference> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesReference> series) {
        this.series = series;
    }

    public List<ErroneousStudy> getErroneous() {
        return erroneous;
    }

    public void setErroneous(List<ErroneousStudy> erroneous) {
        this.erroneous = erroneous;
    }
}
