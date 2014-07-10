package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metkaSearch.commands.searcher.SearcherCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;

import java.util.concurrent.Callable;

public class Searcher implements Callable<IndexerStatusMessage>, SearcherCommandHandler {

    /**
     * Indexer path where this search should be performed
     */
    private final DirectoryManager.DirectoryPath path;

    public Searcher(DirectoryManager.DirectoryPath path) {
        this.path = path;
    }

    @Override
    public boolean addCommand(SearcherCommand command) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public IndexerStatusMessage call() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
