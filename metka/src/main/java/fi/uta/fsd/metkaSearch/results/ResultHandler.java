package fi.uta.fsd.metkaSearch.results;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 * Common interface for all ResultHandlers.
 * Result handlers take in a TopDocs result from a search query and read/parse/modify
 * it in some way to produce a ResultList with sensible results for the executed query.
 * ResultHandler will assume that it has received a TopDocs from the correct query
 */
public interface ResultHandler {
    /**
     * Request handling of given results.
     *
     * @param searcher Caller must provide the IndexSearcher responsible for the TopDocs so that actual documents can be retrieved.
     * @param results TopDocs produced by the executed query
     * @return ResultList containing appropriate results dependant on implementor and given TopDocs
     */
    public ResultList handle(IndexSearcher searcher, TopDocs results);
}
