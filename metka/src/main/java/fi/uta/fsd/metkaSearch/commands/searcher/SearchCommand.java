package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.lucene.search.Query;

/**
 * This is the base interface for all search commands that are targeted at Lucene in Metka software.
 * It defines the most basic SearchCommand that can function providing the requested index location
 * as well as a method for getting a query that can be executed. More complex cases however might
 * require additional information.
 */
public interface SearchCommand {
    /**
     * To what index should this command be directed to
     * @return
     */
    public DirectoryManager.DirectoryPath getPath();

    /**
     * Query to be executed based on the properties given to this command.
     * This is the most challenging part of the search engine since some circumstances might require multiple
     * queries to be executed in sequence to get the correct result. In those cases additional commands should
     * most likely be created out of the initial query request.
     * @return Query to be executed
     */
    public Query getQuery();

    /**
     * What sort of result can you expect from this search command.
     * This should be set by the constructor of each respective SearchCommand implementation.
     * @return
     */
    public ResultList.ResultType getResultType();

    /**
     * This will return a result handler appropriate for the search command.
     * The result handler takes in TopDocs returned by the search and forms
     * a sensible ResultList from them.
     * @return
     */
    public ResultHandler getResulHandler();
}
