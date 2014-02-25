package fi.uta.fsd.metka.mvc.domain.simple.series;

import java.util.List;

public class SeriesSearchData {

    private SeriesSearchSO query;
    private List<String> abbreviations;
    private List<SeriesSearchResultSO> results;

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

    public List<SeriesSearchResultSO> getResults() {
        return results;
    }

    public void setResults(List<SeriesSearchResultSO> results) {
        this.results = results;
    }
}
