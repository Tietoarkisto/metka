package fi.uta.fsd.metkaSearch.commands.searcher.general;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.BooleanResult;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * This class provides information and query necessary for checking value field uniqueness.
 */
public final class ValueFieldUniquenessSearchCommand extends RevisionSearchCommandBase<BooleanResult> {
    public static ValueFieldUniquenessSearchCommand build(Long revisionableId, String key, String value, ConfigurationType type, Language language) throws UnsupportedOperationException {
        //checkPath(path, ConfigurationType.SERIES);
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, language, type.toValue());
        return new ValueFieldUniquenessSearchCommand(path, revisionableId, key, value);
    }

    private final Query query;
    private ValueFieldUniquenessSearchCommand(DirectoryManager.DirectoryPath path, Long revisionableId, String key, String value) {
        super(path, ResultList.ResultType.BOOLEAN);
        // Create new boolean query.
        // key.id not match given revisionableId
        // seriesabbr should match given abbreviation
        // This searches all failure for uniqueness which the handler then reverses
        BooleanQuery bQuery = new BooleanQuery();
        // key.id should be defined somewhere along with status fields
        // TODO: Check precision
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 1, revisionableId, revisionableId, true, true), BooleanClause.Occur.MUST_NOT);
        // Abbreviation field key should really come from somewhere else so that the search can adapt on the fly to changes
        bQuery.add(new TermQuery(new Term(key, value)), BooleanClause.Occur.MUST);

        query = bQuery;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<BooleanResult> getResulHandler() {
        return new ValueFieldAbbreviationUniquenessResultHandler();
    }

    private static class ValueFieldAbbreviationUniquenessResultHandler implements ResultHandler<BooleanResult> {
        @Override
        public ResultList<BooleanResult> handle(IndexSearcher searcher, TopDocs results) {
            ResultList<BooleanResult> list = new ListBasedResultList<>(ResultList.ResultType.BOOLEAN);
            if(searcher == null || results == null) {
                return list;
            }
            if(results.totalHits == 0) {
                // Unique
                list.addResult(new BooleanResult(true));
            } else {
                // Not unique
                list.addResult(new BooleanResult(false));
            }

            return list;
        }
    }
}
