package fi.uta.fsd.metkaSearch.commands.searcher.expert;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.Query;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertRevisionSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    public static ExpertRevisionSearchCommand build(String qry) throws UnsupportedOperationException, QueryNodeException {
        if(StringUtils.isEmpty(qry)) {
            throw new UnsupportedOperationException("Query string was empty, can't form expert query");
        }
        String[] splits = qry.split("\\s", 2);
        IndexerConfigurationType iType = IndexerConfigurationType.REVISION;
        ConfigurationType cType;
        if(ConfigurationType.isValue(splits[0])) {
            cType = ConfigurationType.fromValue(splits[0]);
            qry = splits.length > 1 ? splits[1] : "";
        } else {
            cType = ConfigurationType.STUDY;
        }

        splits = qry.split("\\s", 2);
        String lang;
        if(splits[0].split(":", 2)[0].equals("lang")) {
            lang = splits[0].split(":", 2)[0];
            qry = splits.length > 1 ? splits[1] : "";
        } else {
            lang = "fi";
        }

        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, iType, lang, cType.toValue());
        return new ExpertRevisionSearchCommand(path, qry);
    }

    private Query query;
    private ExpertRevisionSearchCommand(DirectoryManager.DirectoryPath path, String qry) throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);
        //addTextAnalyzer("seriesname");
        Map<String, NumericConfig> nums = new HashMap<>();
        //nums.put("key.id", new NumericConfig(1, new DecimalFormat(), FieldType.NumericType.LONG));

        // TODO: Fill analyzers and numeric field info

        StandardQueryParser parser = new StandardQueryParser(getAnalyzer());
        parser.setNumericConfigMap(nums);
        query = parser.parse(qry, "key.id");
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
