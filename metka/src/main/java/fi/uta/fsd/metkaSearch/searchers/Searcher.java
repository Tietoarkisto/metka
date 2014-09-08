package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;

import java.util.concurrent.Callable;

/**
 * Base class for actual search query execution.
 * Most of the time search queries are executed synchronously but it's implemented as a Thread to allow
 * for asynchronous execution of queries.
 * SearcherComponent should decide between synchronous and asynchronous implementation
 */
public abstract class Searcher<T extends SearchResult> implements Callable<ResultList<T>> {

    private final DirectoryManager manager;

    /**
     * Indexer path where this search should be performed
     */
    private final DirectoryManager.DirectoryPath path;
    /**
     * Command that should be executed
     */
    private final SearchCommand<T> command;

    private final DirectoryInformation indexer;

    protected Searcher(DirectoryManager manager, SearchCommand<T> command) throws UnsupportedOperationException {
        this.manager = manager;
        this.path = command.getPath();
        this.command = command;
        indexer = manager.getIndexDirectory(path, false);
        if(indexer == null) {
            throw new UnsupportedOperationException("Couldn't get an indexer for Searcher with path "+path);
        }
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    public DirectoryInformation getIndexer() {
        return indexer;
    }

    public SearchCommand<T> getCommand() {
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
    public abstract ResultList<T> call() throws Exception;
}
