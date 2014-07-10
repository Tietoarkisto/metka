package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * This class provides information and query necessary for checking series abbreviation uniqueness.
 * This class as with most search commands is final since
 *
 * // TODO: general field uniqueness checker for both inside a revisionable and between revisionables. This implementation is really just a test case.
 */
public final class SeriesBasicSearchCommand extends RevisionSearchCommandBase {
    public static SeriesBasicSearchCommand build(DirectoryManager.DirectoryPath path,
                                                 boolean allowApproved, boolean allowDraft, boolean allowRemoved,
                                                 Long revisionableId, String abbreviation, String name) throws UnsupportedOperationException {
        checkPath(path, ConfigurationType.SERIES);

        return new SeriesBasicSearchCommand(path, allowApproved, allowDraft, allowRemoved, revisionableId, abbreviation, name);
    }

    private final Query query;
    private SeriesBasicSearchCommand(DirectoryManager.DirectoryPath path,
                                     boolean allowApproved, boolean allowDraft, boolean allowRemoved,
                                     Long revisionableId, String abbreviation, String name) {
        super(path, ResultList.ResultType.REVISION);
        // Create new boolean query.
        // For each of allowApproved, allowDraft and allowRemoved if they are false
        // then set a new MUST condition where their respective fields must be false.
        // This allows for filtering based on status of revision or revisionable.
        // If given revisionableId is not null then add a condition where key.id must match given revisionableId
        // If abbreviation or name are non null non empty, then add MUST conditions for
        // seriesabbr and seriesname fields. These should support wildcards but must be tested.
        BooleanQuery bQuery = new BooleanQuery();
        // key.id should be defined somewhere along with status fields
        if(!allowApproved) {
            bQuery.add(new TermQuery(new Term("state.approved", "false")), BooleanClause.Occur.MUST);
        } else {
            bQuery.add(new TermQuery(new Term("state.approved", "true")), BooleanClause.Occur.SHOULD);
        }
        if(!allowDraft) {
            bQuery.add(new TermQuery(new Term("state.draft", "false")), BooleanClause.Occur.MUST);
        } else {
            bQuery.add(new TermQuery(new Term("state.draft", "true")), BooleanClause.Occur.SHOULD);
        }
        if(!allowRemoved) {
            bQuery.add(new TermQuery(new Term("state.removed", "false")), BooleanClause.Occur.MUST);
        } else {
            bQuery.add(new TermQuery(new Term("state.removed", "true")), BooleanClause.Occur.SHOULD);
        }
        if(revisionableId != null) {
            bQuery.add(NumericRangeQuery.newLongRange("key.id", 1, revisionableId, revisionableId, true, true), BooleanClause.Occur.MUST);
        }
        if(!StringUtils.isEmpty(abbreviation)) {
            bQuery.add(new TermQuery(new Term("seriesabbr", abbreviation)), BooleanClause.Occur.MUST);
        }
        if(!StringUtils.isEmpty(name)) {
            bQuery.add(new TermQuery(new Term("seriesname", name)), BooleanClause.Occur.MUST);
        }


        query = bQuery;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler getResulHandler() {
        return new BasicRevisionSearchResultHandler();
    }


}
