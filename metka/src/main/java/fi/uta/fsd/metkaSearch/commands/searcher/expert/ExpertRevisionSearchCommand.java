/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metkaSearch.commands.searcher.expert;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchRequest;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.commands.searcher.RevisionSearchCommandBase;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.NumericConfig;
import org.apache.lucene.queryparser.flexible.standard.parser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanQuery;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.*;

import static fi.uta.fsd.metka.enums.FieldType.INTEGER;
import static fi.uta.fsd.metka.enums.FieldType.REAL;

public class ExpertRevisionSearchCommand extends RevisionSearchCommandBase<RevisionResult> {
    private final static String LANG_TOKE = "lang";

    public static ExpertRevisionSearchCommand build(RevisionSearchRequest request, ConfigurationRepository configurations)
            throws UnsupportedOperationException, QueryNodeException {
        List<String> qrys = new ArrayList<>();

        qrys.add(((!request.isSearchApproved())?"+":"")+"state.approved:"+request.isSearchApproved());
        qrys.add(((!request.isSearchDraft())?"+":"")+"state.draft:"+request.isSearchDraft());
        qrys.add(((!request.isSearchRemoved())?"+":"")+"state.removed:"+request.isSearchRemoved());

        for(String key : request.getValues().keySet()) {
            if(!StringUtils.hasText(request.getByKey(key))) {
                continue;
            }
            qrys.add("+"+key+":"+request.getByKey(key));
        }

        String qryStr = StringUtils.collectionToDelimitedString(qrys, " ");

        //DirectoryManager.DirectoryPath path = DirectoryManager.formPath(false, IndexerConfigurationType.REVISION);
        return ExpertRevisionSearchCommand.build(qryStr, configurations);
    }

    public static ExpertRevisionSearchCommand build(String qry, ConfigurationRepository configurations)
            throws UnsupportedOperationException, QueryNodeException {
        if(!StringUtils.hasText(qry)) {
            throw new UnsupportedOperationException("Query string was empty, can't form expert query");
        }
        ConfigurationType type = extractConfType(qry);
        Configuration conf = null;
        if(type != null) {
            conf = configurations.findLatestConfiguration(type).getRight();
        }
        return new ExpertRevisionSearchCommand(DirectoryManager.formPath(false, IndexerConfigurationType.REVISION), qry, conf);
    }

    public static ExpertRevisionSearchCommand build(String qry, Configuration configuration)
            throws UnsupportedOperationException, QueryNodeException {
        if(!StringUtils.hasText(qry)) {
            throw new UnsupportedOperationException("Query string was empty, can't form expert query");
        }
        return new ExpertRevisionSearchCommand(DirectoryManager.formPath(false, IndexerConfigurationType.REVISION), qry, configuration);
    }

    private static ConfigurationType extractConfType(String qry) throws ParseException {
        if(!StringUtils.hasText(qry)) {
            throw new ParseException(new MessageImpl("EMPTY_QUERY"));
        }
        try {
            if(qry.contains("key.configuration.type:")) {
                qry = qry.substring(qry.indexOf("key.configuration.type:")+"key.configuration.type:".length());
                String[] split = qry.split("\\s");
                if(split.length > 0) {
                    return ConfigurationType.fromValue(split[0]);
                }
            }
        } catch(IllegalArgumentException e) {
            throw new ParseException(new MessageImpl("Illegal ConfigurationType"));
        }
        return null;
    }

    private static Language extractLanguage(String qry) throws ParseException {
        if(!StringUtils.hasText(qry)) {
            throw new ParseException(new MessageImpl("EMPTY_QUERY"));
        }

        if(qry.contains("key.language:")) {
            qry = qry.substring(qry.indexOf("key.language:")+"key.language:".length());
            String[] split = qry.split("\\s|\\)");
            if(split.length > 0) {
                return Language.fromValue(split[0]);
            }
        }
        return null;
    }

    private Query query;

    private ExpertRevisionSearchCommand(DirectoryManager.DirectoryPath path, String qry, Configuration configuration)
            throws QueryNodeException {
        super(path, ResultList.ResultType.REVISION);
        setLanguage(extractLanguage(qry));
        Map<String, NumericConfig> nums = new HashMap<>();

        /*StandardQueryParser parser = new StandardQueryParser(getAnalyzer());*/
        StandardQueryParser parser = new StandardQueryParser();
        parser.setAllowLeadingWildcard(true);
        parser.setAnalyzer(getAnalyzer());
        if(!StringUtils.hasText(qry)) {
            throw new ParseException(new MessageImpl("EMPTY_QUERY"));
        }
        query = parser.parse(qry, "general");

        // No matter if we have configuraion or not we'll parse the query twice. This second parsing will add analyzers for known keys like key.id
        // as well as fields found from configuration if configuration is provided
        addAnalyzersAndConfigs(query, nums, configuration);
        parser.setAnalyzer(getAnalyzer());
        parser.setNumericConfigMap(nums);
        query = parser.parse(qry, "general");
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public ResultHandler<RevisionResult> getResultHandler() {
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
        }/* else if(query instanceof PhraseQuery) {
            addPhraseQuery((PhraseQuery)query, nums, config);
        }*/
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
        // If we have config we can use field configuration to decide how to process the field, otherwise use default analyzer
        if(config != null) {
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
}
