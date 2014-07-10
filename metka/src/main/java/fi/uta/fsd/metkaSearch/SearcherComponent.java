package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.searchers.RevisionSearcher;
import fi.uta.fsd.metkaSearch.searchers.Searcher;
import org.apache.lucene.index.IndexReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class SearcherComponent {

    // Pool for searcher threads.
    private ExecutorService indexerPool = Executors.newCachedThreadPool();

    /**
     * This is a blocking implementation of search execution (search is executed synchronously).
     *
     * @param command
     * @return
     */
    public ResultList executeSearch(SearchCommand command) {
        ResultList results = null;
        try {
            Searcher searcher = build(command);
            Future<ResultList> operation = indexerPool.submit(searcher);
            results = operation.get();
        } catch(UnsupportedOperationException uoe) {
            uoe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        } catch(ExecutionException ee) {
            ee.printStackTrace();
        }
        return results;
    }

    /**
     * Factory method to build a new Searcher for executing given command
     * @param command
     * @return
     */
    public static Searcher build(SearchCommand command) throws IOException, UnsupportedOperationException {
        Searcher searcher = null;
        switch(command.getPath().getType()) {
            case REVISION:
                searcher = RevisionSearcher.build(command);
                break;
        }
        return searcher;
    }
}
