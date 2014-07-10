package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

/**
 * For now this knows how to execute a search command aimed at revision.
 *
 * TODO: Split to single-index and multi-index variants.
 */
public class RevisionSearcher extends Searcher {
    public static RevisionSearcher build(SearchCommand command) throws IOException, UnsupportedOperationException {
        if(command.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Path is not for a REVISION");
        }
        if(command.getPath().getAdditionalParameters() == null || command.getPath().getAdditionalParameters().length == 0 || command.getPath().getAdditionalParameters().length > 1) {
            throw new UnsupportedOperationException("There's too few or too many additional parameters");
        }
        if(!ConfigurationType.isValue(command.getPath().getAdditionalParameters()[0])) {
            throw new UnsupportedOperationException("Additional parameter must be a ConfigurationType");
        }

        return new RevisionSearcher(command);
    }

    private RevisionSearcher(SearchCommand command) throws IOException {
        super(command);
    }

    @Override
    public ResultList call() throws Exception {
        IndexReader reader = getIndexer().getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs results = searcher.search(getCommand().getQuery(), 100);
        ResultHandler handler = getCommand().getResulHandler();
        return handler.handle(searcher, results);
    }
}
