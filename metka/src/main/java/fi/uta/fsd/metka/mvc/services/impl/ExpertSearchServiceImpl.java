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

package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.*;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpertSearchServiceImpl implements ExpertSearchService {
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private SavedSearchRepository savedSearch;

    @Override public ExpertSearchQueryResponse performQuery(ExpertSearchQueryRequest request) throws Exception {
        ExpertSearchQueryResponse response = new ExpertSearchQueryResponse();
        response.setOperation(ExpertSearchOperation.QUERY);
        if(!StringUtils.hasText(request.getQuery())) {
            response.setResult(ReturnResult.EMPTY_QUERY);
            return response;
        }

        // Subquery support
        // For now subqueries are denoted by enclosing them within <TAG>{}<TAG> where tag is an indicator of the value set of returned results
        // Currently subqueries support key.id: ID{}ID and key: KEY{}KEY
        // Later on more complex subqueries might be supported to allow for freely selected return parameters for example

        // Subquery process is really simple
        // The query is send to a recursive method that searches it for queries enclosed in S{ }S
        // If it finds any it calls itself with that subquery
        // This is continued for as long as there are subqueries
        Query query = new Query(TagType.ROOT, 0, request.getQuery().length());
        findQueries(request.getQuery(), query);

        // After that the queries are performed in a depth first manner
        Pair<ReturnResult, ResultList<RevisionResult>> queryResults = performQuery(request.getQuery(), query);

        Logger.info(getClass(), "QUERY: " + request.getQuery() + " | Results: " + queryResults.getRight().getResults().size());

        if(queryResults.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            Logger.error(ExpertSearchServiceImpl.class, "Search failed with the result "+queryResults.getLeft()+". Query was: "+request.getQuery());
            response.setResult(queryResults.getLeft());
            return response;
        }

        ResultList<RevisionResult> results = collectResults(queryResults.getRight());
        if(results.getResults().size() > LuceneConfig.MAX_RETURNED_RESULTS) {
            results.getResults().removeAll(results.getResults().subList(LuceneConfig.MAX_RETURNED_RESULTS, results.getResults().size()));
            response.setResults(results);
            response.setResult(ReturnResult.RESULT_SET_TOO_LARGE);
        } else {
            response.setResults(results);
            response.setResult(ReturnResult.OPERATION_SUCCESSFUL);
        }
        return response;
    }

    private ResultList<RevisionResult> collectResults(ResultList<RevisionResult> resultList) {
        if(resultList == null) {
            return resultList;
        }

        resultList.sort(new Comparator<RevisionResult>() {
            @Override
            public int compare(RevisionResult o1, RevisionResult o2) {
                if(o1.getId().compareTo(o2.getId()) == 0) {
                    return o1.getNo().compareTo(o2.getNo());
                } else {
                    return o1.getId().compareTo(o2.getId());
                }
            }
        });

        return resultList;
    }

    private Pair<ReturnResult, ResultList<RevisionResult>> performQuery(String qryStr, Query query) {
        // If subquery was found then the result provided by the reqursion is formed into a grouping and the whole subquery is replaced by it
        // After replacing the subquery with the result grouping the query is then run and the results are returned
        // This process continues untill all subqueries and the main query have been executed and the last result set is returned to the caller
        if(!query.getQueries().isEmpty()) {
            int difference = 0;
            for(Query sq : query.getQueries()) {
                String subq = qryStr.substring(sq.start+difference, sq.end+difference);
                Pair<ReturnResult, ResultList<RevisionResult>> queryResults = performQuery(subq, sq);
                if(queryResults.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                    return queryResults;
                }
                if(queryResults.getRight().getResults().isEmpty()) {
                    return new ImmutablePair<>(ReturnResult.EMPTY_SUBQUERY, null);
                }

                String replc = "(";
                for(RevisionResult rr : queryResults.getRight().getResults()) {
                    if(replc.length() > 1) {
                        replc += " ";
                    }
                    switch(sq.tag) {
                        case KEY:
                            replc += rr.getId()+"-"+rr.getNo();
                            break;
                        case ID:
                            replc += rr.getId();
                            break;
                    }

                }
                replc += ")";
                String newQry = qryStr.substring(0, sq.start+difference-sq.tag.getL());
                newQry += replc;
                newQry += qryStr.substring(sq.end+difference+sq.tag.getL());
                qryStr = newQry;

                difference += (replc.length()-(sq.tag.getL()*2)) - (sq.end-sq.start);
            }
        }
        return performSearch(qryStr);
    }

    private Pair<ReturnResult, ResultList<RevisionResult>> performSearch(String qryStr) {
        ReturnResult response;
        SearchCommand<RevisionResult> command = null;
        try {
            command = ExpertRevisionSearchCommand.build(qryStr, configurations);
        } catch(QueryNodeException e) {
            Logger.error(getClass(), "Exception while forming search command.", e);
            switch(e.getMessageObject().getKey()) {
                case "EMPTY_QUERY":
                    response = ReturnResult.EMPTY_QUERY;
                    break;
                case "MALFORMED_LANGUAGE":
                    response = ReturnResult.MALFORMED_LANGUAGE;
                    break;
                case "INVALID_SYNTAX_CANNOT_PARSE":
                    response = ReturnResult.MALFORMED_QUERY;
                    break;
                default:
                    response = ReturnResult.OPERATION_FAIL;
                    break;
            }
            return new ImmutablePair<>(response, null);
        } catch(Exception e) {
            Logger.error(getClass(), "Exception while forming search command.", e);
            throw e;
        }
        ResultList<RevisionResult> results = searcher.executeSearch(command);
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, results);
    }

    @Override public ExpertSearchListResponse listSavedSearcher() {
        ExpertSearchListResponse response = new ExpertSearchListResponse();
        List<SavedExpertSearchItem> items = savedSearch.listSavedSearches();
        for(SavedExpertSearchItem item : items) {
            response.getQueries().add(item);
        }
        return response;
    }

    @Override public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem item) {
        return savedSearch.saveExpertSearch(item);
    }

    @Override public void removeExpertSearch(Long id) {
        savedSearch.removeExpertSearch(id);
    }

    private enum TagType {
        ROOT("", "", 0),
        KEY(":KEY\\{", "}KEY(?=\\s|\\z)", 4),
        ID(":ID\\{", "}ID(?=\\s|\\z)", 3);

        private final String start;
        private final String end;
        private final int l;

        TagType(String start, String end, int l) {
            this.start = start;
            this.end = end;
            this.l = l;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public int getL() {
            return l;
        }
    }

    private static List<TagType> tags = new ArrayList<>();

    static {
        tags.add(TagType.KEY);
        tags.add(TagType.ID);
    }
    private static String pStart = ":S\\{";
    private static String pEnd = "}S(?=\\s|\\z)";

    private void findQueries(String qry, Query query) {
        for(TagType tag : tags) {
            Pattern p = Pattern.compile(tag.start);

            Matcher starts = p.matcher(qry);
            p = Pattern.compile(tag.end);
            Matcher ends = p.matcher(qry);
            List<Integer> is = new ArrayList<Integer>();
            List<Integer> ie = new ArrayList<Integer>();
            while(starts.find()) {
                is.add(starts.end());
            }
            while(ends.find()) {
                ie.add(ends.start());
            }

            int depth = 0;
            int rs = 0;
            for(int i=0; i<qry.length(); i++) {
                if(is.contains(i)) {
                    depth++;
                    if(depth == 1) {
                        rs = i;
                    }
                }
                if(ie.contains(i)) {
                    depth--;
                    if(depth == 0) {
                        boolean addQuery = true;
                        for(Query q : query.queries) {
                            if(q.start < rs && q.end > i) {
                                addQuery = false;
                                break;
                            }
                        }
                        if(addQuery) {
                            Query subquery = new Query(tag, rs, i);
                            query.queries.add(subquery);
                            findQueries(qry.substring(rs, i), subquery);
                        }
                    }
                }
            }
        }
    }

    private static class Query {
        private final TagType tag;
        private final int start;
        private final int end;
        private final List<Query> queries = new ArrayList<>();

        public Query(TagType tag, int start, int end) {
            this.tag = tag;
            this.start = start;
            this.end = end;
        }

        public TagType getTag() {
            return tag;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public List<Query> getQueries() {
            return queries;
        }

        // Depug method
        public void print(String text) {
            System.out.print("Region ["+start+"|"+end+"]: ");
            System.out.println(text);
            if(queries.size() > 0) {
                System.out.println("With subregions: ");
            }
            for(Query query : queries) {
                query.print(text.substring(query.start, query.end));
            }
        }
    }
}
