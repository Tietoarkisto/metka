package fi.uta.fsd.metkaSearch.commands.searcher.expert;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static fi.uta.fsd.metka.enums.FieldType.*;

public class ExpertRevisionSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    public static ExpertRevisionSearchCommand build(String qry, ConfigurationRepository configurations) throws UnsupportedOperationException, QueryNodeException {
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
            lang = Language.DEFAULT.toValue();
        }

        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, iType, lang, cType.toValue());
        return new ExpertRevisionSearchCommand(path, qry, configurations);
    }

    private Query query;
    private ExpertRevisionSearchCommand(DirectoryManager.DirectoryPath path, String qry, ConfigurationRepository configurations) throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);
        Pair<ReturnResult, Configuration> pair = configurations.findLatestConfiguration(ConfigurationType.fromValue(path.getAdditionalParameters()[0]));

        //addTextAnalyzer("seriesname");
        Map<String, NumericConfig> nums = new HashMap<>();
        // TODO: Add default numeric fields like key.id
        //nums.put("key.id", new NumericConfig(1, new DecimalFormat(), FieldType.NumericType.LONG));

        // TODO: Fill analyzers and numeric field info

        /*StandardQueryParser parser = new StandardQueryParser(getAnalyzer());*/
        StandardQueryParser parser = new StandardQueryParser();
        if(pair.getLeft() == ReturnResult.CONFIGURATION_FOUND) {
            // If we're in config mode we need to parse the query twice, once to get all the fields in the query and second time with the actual numeric configs and analyzers
            query = parser.parse(qry, "general");

            addAnalyzersAndConfigs(query, nums, pair.getRight());

            parser.setAnalyzer(getAnalyzer());
            parser.setNumericConfigMap(nums);
        }
        query = parser.parse(qry, "general");
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<RevisionResult> getResulHandler() {
        return new BasicRevisionSearchResultHandler();
    }

    private void addAnalyzersAndConfigs(Query query, Map<String, NumericConfig> nums, Configuration config) {
        if(query instanceof TermQuery) {
            addTermQuery((TermQuery)query, nums, config);
        } else if(query instanceof BooleanQuery) {
            addBooleanQuery((BooleanQuery)query, nums, config);
        }
    }

    private void addBooleanQuery(BooleanQuery query, Map<String, NumericConfig> nums, Configuration config) {
        for(BooleanClause clause : query.getClauses()) {
            addAnalyzersAndConfigs(clause.getQuery(), nums, config);
        }
    }

    private void addTermQuery(TermQuery query, Map<String, NumericConfig> nums, Configuration config) {
        // TODO: Reference field end points as well as some automated values are not found on given configuration but instead need to be found through reference handling
        String key = query.getTerm().field();
        switch(key) {
            case "key.id":
                nums.put(key, new NumericConfig(4, new DecimalFormat(), FieldType.NumericType.LONG));
                return;
            case "key.no":
                nums.put(key, new NumericConfig(4, new DecimalFormat(), FieldType.NumericType.LONG));
                return;
            case "key.configuration.version":
                nums.put(key, new NumericConfig(4, new DecimalFormat(), FieldType.NumericType.LONG));
                return;
        }
        String[] splits = key.split(".");
        if(splits.length == 0) {
            splits = new String[1];
            splits[0] = key;
        }
        String start = splits[0];
        if(start.equals("key") || start.equals("state")) {

            return;
        }

        Field field = config.getField(splits[splits.length-1]);
        if(field == null) {
            addKeywordAnalyzer(key);
            return;
        }
        if(field.getType() == STRING || field.getType() == CONCAT) {
            if(!field.getExact()) {
                addTextAnalyzer(key);
            } else {
                addWhitespaceAnalyzer(key);
            }
        } else if(field.getType() == INTEGER) {
            nums.put(key, new NumericConfig(1, new DecimalFormat(), FieldType.NumericType.LONG));
        } else if(field.getType() == REAL) {
            nums.put(key, new NumericConfig(1, new DecimalFormat(), FieldType.NumericType.DOUBLE));
        }
    }
}
