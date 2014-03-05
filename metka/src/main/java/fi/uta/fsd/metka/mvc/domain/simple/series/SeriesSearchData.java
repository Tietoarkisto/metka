package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.mvc.domain.simple.transfer.SearchResult;

import java.util.List;

public class SeriesSearchData {

    private SeriesSearchSO query;
    private List<String> abbreviations;
    private List<SearchResult> results;

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

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }
}
