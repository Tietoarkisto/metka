package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For now this knows how to execute a search command aimed at revision.
 *
 * TODO: Split to single-index and multi-index variants.
 */
public class RevisionSearcher<T extends SearchResult> extends Searcher<T> {
    private static Logger logger = LoggerFactory.getLogger(RevisionSearcher.class);

    public static <T extends SearchResult> RevisionSearcher<T> build(SearchCommand<T> command) throws UnsupportedOperationException {
        if(command.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Path is not for a REVISION");
        }
        if(command.getPath().getAdditionalParameters() == null || command.getPath().getAdditionalParameters().length == 0 || command.getPath().getAdditionalParameters().length > 1) {
            throw new UnsupportedOperationException("There's too few or too many additional parameters");
        }
        if(!ConfigurationType.isValue(command.getPath().getAdditionalParameters()[0])) {
            throw new UnsupportedOperationException("Additional parameter must be a ConfigurationType");
        }
        logger.info("Building new RevisionSearcher for path "+command.getPath().toString());
        return new RevisionSearcher<T>(command);
    }

    private RevisionSearcher(SearchCommand<T> command) throws UnsupportedOperationException {
        super(command);
    }

    @Override
    public ResultList<T> call() throws Exception {
        logger.info("RevisionSearcher is acquiring an IndexReader");
        IndexReader reader = getIndexer().getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        logger.info("RevisionSearcher is performing the following query: "+getCommand().getQuery().toString());
        TopDocs results = searcher.search(getCommand().getQuery(), 100);
        ResultHandler<T> handler = getCommand().getResulHandler();
        return handler.handle(searcher, results);
    }
}
