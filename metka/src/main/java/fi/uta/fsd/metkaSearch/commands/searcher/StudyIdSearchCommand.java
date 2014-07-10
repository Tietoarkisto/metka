package fi.uta.fsd.metkaSearch.commands.searcher;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public final class StudyIdSearchCommand extends RevisionSearchCommandBase {
    public static StudyIdSearchCommand build(DirectoryManager.DirectoryPath path, String studyId) {
        checkPath(path, ConfigurationType.STUDY);

        return new StudyIdSearchCommand(path, studyId);
    }

    private Query query;

    private StudyIdSearchCommand(DirectoryManager.DirectoryPath path, String studyId) {
        super(path, ResultList.ResultType.REVISION);

        BooleanQuery bQuery = new BooleanQuery();
        bQuery.add(new TermQuery(new Term("studyid", studyId)), BooleanClause.Occur.MUST);
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
