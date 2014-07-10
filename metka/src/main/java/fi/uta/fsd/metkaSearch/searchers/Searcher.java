package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultList;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Base class for actual search query execution.
 * Most of the time search queries are executed synchronously but it's implemented as a Thread to allow
 * for asynchronous execution of queries.
 * SearcherComponent should decide between synchronous and asynchronous implementation
 */
public abstract class Searcher implements Callable<ResultList> {

    /**
     * Indexer path where this search should be performed
     */
    private final DirectoryManager.DirectoryPath path;
    /**
     * Command that should be executed
     */
    private final SearchCommand command;

    private final DirectoryInformation indexer;

    protected Searcher(SearchCommand command) throws IOException {
        this.path = command.getPath();
        this.command = command;
        indexer = DirectoryManager.getIndexDirectory(path);
    }

    public DirectoryInformation getIndexer() {
        return indexer;
    }

    public SearchCommand getCommand() {
        return command;
    }

    /**
     * Callable interface implementation.
     * For now search doesn't need any common functions since there's
     * one Searcher per query
     * @return
     * @throws Exception
     */
    @Override
    public abstract ResultList call() throws Exception;
}
