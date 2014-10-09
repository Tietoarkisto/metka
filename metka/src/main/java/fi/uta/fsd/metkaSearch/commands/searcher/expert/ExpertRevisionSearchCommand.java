package fi.uta.fsd.metkaSearch.commands.searcher.expert;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchRequest;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.ResultHandler;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanQuery;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.uta.fsd.metka.enums.FieldType.*;

public class ExpertRevisionSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    public static ExpertRevisionSearchCommand build(RevisionSearchRequest request, Configuration configuration)
            throws UnsupportedOperationException, QueryNodeException {
        List<String> qrys = new ArrayList<>();

        qrys.add(((!request.isSearchApproved())?"+":"")+"state.approved:"+request.isSearchApproved());
        qrys.add(((!request.isSearchDraft())?"+":"")+"state.draft:"+request.isSearchDraft());
        qrys.add(((!request.isSearchRemoved())?"+":"")+"state.removed:"+request.isSearchRemoved());

        for(String key : request.getValues().keySet()) {
            if(!StringUtils.hasText(request.getByKey(key))) {
                continue;
            }
            Field field = configuration.getField(key);
            if(field != null && field.getExact()) {
                qrys.add("+"+key+":\""+request.getByKey(key)+"\"");
            } else {
                qrys.add("+"+key+":("+request.getByKey(key)+")");
            }
        }

        String qryStr = StringUtils.collectionToDelimitedString(qrys, " ");

        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, Language.DEFAULT, request.getType().toValue());
        return new ExpertRevisionSearchCommand(path, qryStr, configuration);
    }

    public static ExpertRevisionSearchCommand build(String qry, ConfigurationRepository configurations) throws UnsupportedOperationException, QueryNodeException {
        if(!StringUtils.hasText(qry)) {
            throw new UnsupportedOperationException("Query string was empty, can't form expert query");
        }
        Pair<DirectoryManager.DirectoryPath, String> pathPair = extractPath(qry);
        Pair<ReturnResult, Configuration> pair = configurations.findLatestConfiguration(ConfigurationType.fromValue(pathPair.getLeft().getAdditionalParameters()[0]));
        return new ExpertRevisionSearchCommand(pathPair.getLeft(), pathPair.getRight(), pair.getRight());
    }

    public static ExpertRevisionSearchCommand build(String qry, Configuration configuration) throws UnsupportedOperationException, QueryNodeException {
        if(!StringUtils.hasText(qry)) {
            throw new UnsupportedOperationException("Query string was empty, can't form expert query");
        }
        Pair<DirectoryManager.DirectoryPath, String> pathPair = extractPath(qry);
        return new ExpertRevisionSearchCommand(pathPair.getLeft(), pathPair.getRight(), configuration);
    }

    private static Pair<DirectoryManager.DirectoryPath, String> extractPath(String qry) {
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
        Language lang;
        if(splits[0].split(":", 2)[0].equals("lang")) {
            lang = Language.fromValue(splits[0].split(":", 2)[1]);
            qry = splits.length > 1 ? splits[1] : "";
        } else {
            lang = Language.DEFAULT;
        }

        DirectoryManager.DirectoryPath path =  DirectoryManager.formPath(false, iType, lang, cType.toValue());
        return new ImmutablePair<>(path, qry);
    }

    private Query query;

    private ExpertRevisionSearchCommand(DirectoryManager.DirectoryPath path, String qry, Configuration configuration) throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);

        //addTextAnalyzer("seriesname");
        Map<String, NumericConfig> nums = new HashMap<>();

        /*StandardQueryParser parser = new StandardQueryParser(getAnalyzer());*/
        StandardQueryParser parser = new StandardQueryParser();
        parser.setAllowLeadingWildcard(true);
        if(configuration != null) {
            // If we're in config mode we need to parse the query twice, once to get all the fields in the query and second time with the actual numeric configs and analyzers
            query = parser.parse(qry, "general");

            addAnalyzersAndConfigs(query, nums, configuration);
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
            addTermQuery((TermQuery) query, nums, config);
        } else if(query instanceof MultiTermQuery) {
            addMultiQuery((MultiTermQuery)query, nums, config);
        } else if(query instanceof SpanQuery) {
            addSpanQuery((SpanQuery)query, nums, config);
        } else if(query instanceof BooleanQuery) {
            addBooleanQuery((BooleanQuery)query, nums, config);
        }
        // TODO: Some query types might still be missed in which case they don't have correct numeric configurations etc.
    }

    private void addBooleanQuery(BooleanQuery query, Map<String, NumericConfig> nums, Configuration config) {
        for(BooleanClause clause : query.getClauses()) {
            addAnalyzersAndConfigs(clause.getQuery(), nums, config);
        }
    }

    private void addTermQuery(TermQuery query, Map<String, NumericConfig> nums, Configuration config) {
        addField(query.getTerm().field(), nums, config);
    }

    private void addMultiQuery(MultiTermQuery query, Map<String, NumericConfig> nums, Configuration config) {
        addField(query.getField(), nums, config);
    }

    private void addSpanQuery(SpanQuery query, Map<String, NumericConfig> nums, Configuration config) {
        addField(query.getField(), nums, config);
    }

    private void addField(String key, Map<String, NumericConfig> nums, Configuration config) {
        // TODO: Reference field end points as well as some automated values are not found on given configuration but instead need to be found through reference handling
        switch(key) {
            case "key.id":
            case "key.no":
            case "key.configuration.version":
                nums.put(key, new NumericConfig(LuceneConfig.PRECISION_STEP, new DecimalFormat(), FieldType.NumericType.LONG));
                return;
        }
        String[] splits = key.split("\\.");
        if(splits.length == 0) {
            splits = new String[1];
            splits[0] = key;
        }
        String start = splits[0];
        if(start.equals("key") || start.equals("state")) {
            return;
        }

        if(splits[splits.length-1].equals("value")) {
            addKeywordAnalyzer(key);
            return;
        }

        Field field = config.getField(splits[splits.length-1]);
        if(field == null) {
            // Search with indexName
            for(Field f : config.getFields().values()) {
                if(f.getIndexName() != null && f.getIndexName().equals(splits[splits.length-1])) {
                    field = f;
                    break;
                }
            }
        }
        if(field == null) {
            addKeywordAnalyzer(key);
            return;
        }
        if(field.getType() == INTEGER) {
            nums.put(key, new NumericConfig(LuceneConfig.PRECISION_STEP, new DecimalFormat(), FieldType.NumericType.LONG));
        } else if(field.getType() == REAL) {
            nums.put(key, new NumericConfig(LuceneConfig.PRECISION_STEP, new DecimalFormat(), FieldType.NumericType.DOUBLE));
        } else {
            if (!field.getExact()) {
                addTextAnalyzer(key);
            } else {
                addKeywordAnalyzer(key);
            }
        }
    }
}
