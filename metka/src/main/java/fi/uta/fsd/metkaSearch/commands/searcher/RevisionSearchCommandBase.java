package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveKeywordAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.CaseInsensitiveWhitespaceAnalyzer;
import fi.uta.fsd.metkaSearch.analyzer.FinnishVoikkoAnalyzer;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Special case of SearchCommandBase
 * This should be used as base for all SearchCommands that target Revision indexes.
 * It contains the target ConfigurationType as an additional parameter and searchers will use this to find correct index configuration
 * so that they can use correct Analyzers for fields.
 * It is assumed that factory methods for the different SearchCommands will check the validity of the configuration type in the path and so
 * we can just extract it from the path while assuming that it is present
 */
public abstract class RevisionSearchCommandBase<T extends SearchResult> extends SearchCommandBase<T> {
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
    private final Map<String, Analyzer> analyzers = new HashMap<>();

    protected RevisionSearchCommandBase(DirectoryManager.DirectoryPath path, ResultList.ResultType resultType) {
        super(path, resultType);

        this.configurationType = ConfigurationType.fromValue(path.getAdditionalParameters()[0]);
    }

    public ConfigurationType getConfigurationType() {
        return configurationType;
    }

    protected void addKeywordAnalyzer(String key) {
        analyzers.put(key, CaseInsensitiveKeywordAnalyzer.ANALYZER);
    }

    protected void addWhitespaceAnalyzer(String key) {
        analyzers.put(key, CaseInsensitiveWhitespaceAnalyzer.ANALYZER);
    }

    protected void addTextAnalyzer(String key) {
        if(getPath().getLanguage().equals(Language.DEFAULT.toValue())) {
            analyzers.put(key, FinnishVoikkoAnalyzer.ANALYZER);
        } else {
            // Add some other tokenizing analyzer if StandardAnalyzer is not enough
            analyzers.put(key, new StandardAnalyzer(LuceneConfig.USED_VERSION));
        }
    }

    protected Analyzer getAnalyzer() {
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(LuceneConfig.USED_VERSION), analyzers);
        return analyzer;
    }

    /*@Override
    public ResultHandler<T> getResulHandler() {
        return new BasicRevisionSearchResultHandler();
    }*/

    protected static class BasicRevisionSearchResultHandler implements ResultHandler<RevisionResult> {
        public BasicRevisionSearchResultHandler() {}

        @Override
        public ResultList<RevisionResult> handle(IndexSearcher searcher, TopDocs results) {
            Logger.debug(BasicRevisionSearchResultHandler.class, "Handling results and transforming them to RevisionResult list");
            ResultList<RevisionResult> list = new ListBasedResultList<>(ResultList.ResultType.REVISION);
            if(searcher == null || results == null) {
                return list;
            }
            for(ScoreDoc doc : results.scoreDocs) {
                try {
                    Document document = searcher.doc(doc.doc);
                    IndexableField field = document.getField("key.id");
                    Long id = null;
                    Long no = null;
                    if(field != null) {
                        id = field.numericValue().longValue();
                    }
                    field = document.getField("key.no");
                    if(field != null) {
                        no = field.numericValue().longValue();
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
