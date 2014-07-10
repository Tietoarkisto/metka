package fi.uta.fsd.metkaSearch.results;

/**
 * Common interface for all search results in Metka software.
 * Only defines the type of result and should be used to allow common add method for Result List
 */
public interface SearchResult {
    public ResultList.ResultType getType();
}
