package fi.uta.fsd.metkaSearch.commands.searcher.series;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * This class provides information and query necessary for checking series abbreviation uniqueness.
 * This class as with most search commands is final since
 *
 * // TODO: general field uniqueness checker for both inside a revisionable and between revisionables. This implementation is really just a test case.
 */
public final class SeriesAbbreviationUniquenessSearchCommand extends RevisionSearchCommandBase<BooleanResult> {
    public static SeriesAbbreviationUniquenessSearchCommand build(String language, Long revisionableId, String abbreviation) throws UnsupportedOperationException {
        //checkPath(path, ConfigurationType.SERIES);
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, language, ConfigurationType.SERIES.toValue());
        return new SeriesAbbreviationUniquenessSearchCommand(path, revisionableId, abbreviation);
    }

    private final Query query;
    private SeriesAbbreviationUniquenessSearchCommand(DirectoryManager.DirectoryPath path, Long revisionableId, String abbreviation) {
        super(path, ResultList.ResultType.BOOLEAN);
        // Create new boolean query.
        // key.id not match given revisionableId
        // seriesabbr should match given abbreviation
        // This searches all failure for uniqueness which the handler then reverses
        BooleanQuery bQuery = new BooleanQuery();
        // key.id should be defined somewhere along with status fields
        bQuery.add(NumericRangeQuery.newLongRange("key.id", 1, revisionableId, revisionableId, true, true), BooleanClause.Occur.MUST_NOT);
        // Abbreviation field key should really come from somewhere else so that the search can adapt on the fly to changes
        bQuery.add(new TermQuery(new Term("seriesabbr", abbreviation)), BooleanClause.Occur.MUST);

        query = bQuery;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<BooleanResult> getResulHandler() {
        return new SeriesAbbreviationUniquenessResultHandler();
    }

    private static class SeriesAbbreviationUniquenessResultHandler implements ResultHandler<BooleanResult> {
        @Override
        public ResultList<BooleanResult> handle(IndexSearcher searcher, TopDocs results) {
            ResultList<BooleanResult> list = new ListBasedResultList<>(ResultList.ResultType.BOOLEAN);
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
