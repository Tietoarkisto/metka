package fi.uta.fsd.metkaSearch.commands.searcher.study;

import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Deprecated
public class StudyBasicSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    // TODO: Factory

    private final Query query;
    // TODO: Parameters
    private StudyBasicSearchCommand(DirectoryManager.DirectoryPath path) throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);

        Map<String, NumericConfig> nums = new HashMap<>();
        List<String> qrys = new ArrayList<>();

        // TODO: Create queries

        StandardQueryParser parser = new StandardQueryParser(getAnalyzer());
        parser.setNumericConfigMap(nums);
        query = parser.parse(StringUtils.join(qrys, " "), "general");
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<RevisionResult> getResulHandler() {
        return new BasicRevisionSearchResultHandler();
    }
}
