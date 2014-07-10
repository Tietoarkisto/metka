package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

/**
 * Special case of SearchCommandBase
 * This should be used as base for all SearchCommands that target Revision indexes.
 * It contains the target ConfigurationType as an additional parameter and searchers will use this to find correct index configuration
 * so that they can use correct Analyzers for fields.
 * It is assumed that factory methods for the different SearchCommands will check the validity of the configuration type in the path and so
 * we can just extract it from the path while assuming that it is present
 */
public abstract class RevisionSearchCommandBase extends SearchCommandBase {
    protected static void checkPath(DirectoryManager.DirectoryPath path, ConfigurationType type) throws UnsupportedOperationException {
        if(path.getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Given path is not for REVISION index");
        }
        if(path.getAdditionalParameters() == null || path.getAdditionalParameters().length == 0 || path.getAdditionalParameters().length > 1) {
            throw new UnsupportedOperationException("No ConfigurationType or too many additional parameters given");
        }
        if(ConfigurationType.fromValue(path.getAdditionalParameters()[0]) != type) {
            throw new UnsupportedOperationException("Given ConfigurationType is not SERIES");
        }
    }

    private final ConfigurationType configurationType;
    protected RevisionSearchCommandBase(DirectoryManager.DirectoryPath path, ResultList.ResultType resultType) {
        super(path, resultType);

        this.configurationType = ConfigurationType.fromValue(path.getAdditionalParameters()[0]);
    }

    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    protected static class BasicRevisionSearchResultHandler implements ResultHandler {
        @Override
        public ResultList handle(IndexSearcher searcher, TopDocs results) {
            ResultList list = new ListBasedResultList(ResultList.ResultType.REVISION);
            for(ScoreDoc doc : results.scoreDocs) {
                try {
                    Document document = searcher.doc(doc.doc);
                    IndexableField field = document.getField("key.id");
                    Long id = null;
                    Integer no = null;
                    if(field != null) {
                        id = field.numericValue().longValue();
                    }
                    field = document.getField("key.no");
                    if(field != null) {
                        no = field.numericValue().intValue();
                    }
                    list.addResult(new RevisionResult(id, no));
                } catch(IOException ioe) {
                    list.addResult(new RevisionResult(null, null));
                }
            }

            return list;
        }
    }
}
