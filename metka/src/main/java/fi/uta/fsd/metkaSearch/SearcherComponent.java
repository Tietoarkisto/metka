package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import fi.uta.fsd.metkaSearch.searchers.RevisionSearcher;
import fi.uta.fsd.metkaSearch.searchers.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.*;

@Service
public class SearcherComponent {
    private static final Logger logger = LoggerFactory.getLogger(SearcherComponent.class);

    // Pool for searcher threads.
    private ExecutorService indexerPool = Executors.newCachedThreadPool();

    /**
     * This is a blocking implementation of search execution (search is executed synchronously).
     *
     * @param command
     * @return
     */
    public <T extends SearchResult> ResultList<T> executeSearch(SearchCommand<T> command) {
        ResultList<T> results = null;
        try {
            Searcher<T> searcher = build(command);
            Future<ResultList<T>> operation = indexerPool.submit(searcher);
            results = operation.get();
        } catch(Exception e) {
            logger.error("Excetpion while executing search command.", e);
        }
        return results;
    }

    /**
     * Factory method to build a new Searcher for executing given command
     * @param command
     * @return
     */
    public static <T extends SearchResult> Searcher<T> build(SearchCommand<T> command) throws IOException, UnsupportedOperationException {
        Searcher<T> searcher = null;
        switch(command.getPath().getType()) {
            case REVISION:
                searcher = RevisionSearcher.build(command);
                break;
            default:
                searcher = null;
                break;
        }
        return searcher;
    }
}
