package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.mvc.domain.simple.transfer.SearchResult;

import java.util.List;

public class StudySearchData {
    private StudySearchSO query;
    private List<SearchResult> results;
    private List<SeriesReference> series;
    private List<ErroneousStudy> erroneous;

    public StudySearchSO getQuery() {
        return query;
    }

    public void setQuery(StudySearchSO query) {
        this.query = query;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
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
