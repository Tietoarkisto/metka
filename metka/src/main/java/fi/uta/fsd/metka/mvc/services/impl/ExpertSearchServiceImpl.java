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
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.expert.*;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
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
        // For now subqueries are denoted by enclosing them within SUB{ }SUB
        // For now subqueries are really simple, only supporting key.id matching (or rather they will return a grouped key.id result to be used with a field)
        // Later on more complex subqueries might be supported to allow for freely selected return parameters for example

        // Subquery process is really simple
        // The query is send to a recursive method that searches it for queries enclosed in S{ }S
        // If it finds any it calls itself with that subquery
        // This is continued for as long as there are subqueries
        Query query = new Query(0, request.getQuery().length());
        findQueries(request.getQuery(), query);

        // After that the queries are performed in a depth first manner
        Pair<ReturnResult, List<RevisionResult>> queryResults = performQuery(request.getQuery(), query);

        if(queryResults.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            Logger.error(ExpertSearchServiceImpl.class, "Search failed with the result "+queryResults.getLeft()+". Query was: "+request.getQuery());
            response.setResult(queryResults.getLeft());
            return response;
        }

        for(RevisionResult result : queryResults.getRight()) {
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(result.getId());
            if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                Logger.warning(getClass(), "Revisionable was not found for id "+result.getId());
                continue;
            }
            Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(result.getId(), result.getNo().intValue());
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.warning(getClass(), "Couldn't find a revision for search result "+result.toString());
                continue;
            }
            RevisionableInfo info = infoPair.getRight();
            RevisionData revision = pair.getRight();
            ExpertSearchRevisionQueryResult qr = new ExpertSearchRevisionQueryResult();
            if(info.getRemoved()) {
                qr.setState(UIRevisionState.REMOVED);
            } else {
                qr.setState(UIRevisionState.fromRevisionState(revision.getState()));
            }
            qr.setId(revision.getKey().getId());
            qr.setNo(revision.getKey().getNo());
            qr.setType(revision.getConfiguration().getType());
            // TODO: Maybe generalize this better
            switch(revision.getConfiguration().getType()) {
                case STUDY: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.TITLE));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(result.getLanguage()));
                    }
                    break;
                }
                case SERIES: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.SERIESNAME));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(result.getLanguage()));
                    }
                    break;
                }
                case PUBLICATION: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.PUBLICATIONTITLE));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(result.getLanguage()));
                    }
                    break;
                }
                case STUDY_VARIABLE: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.VARLABEL));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(result.getLanguage()));
                    }
                    break;
                }
                case STUDY_ATTACHMENT: {
                    Pair<StatusCode, ValueDataField> field = revision.dataField(ValueDataFieldCall.get(Fields.FILE));
                    if(field.getLeft() == StatusCode.FIELD_FOUND) {
                        qr.setTitle(field.getRight().getActualValueFor(result.getLanguage()));
                    }
                    break;
                }
            }
            response.getResults().add(qr);
        }
        response.setResult(ReturnResult.OPERATION_SUCCESSFUL);
        return response;
    }

    private Pair<ReturnResult, List<RevisionResult>> performQuery(String qryStr, Query query) {
        // If subquery was found then the result provided by the reqursion is formed into a grouping and the whole subquery is replaced by it
        // After replacing the subquery with the result grouping the query is then run and the results are returned
        // This process continues untill all subqueries and the main query have been executed and the last result set is returned to the caller
        if(!query.getQueries().isEmpty()) {
            int difference = 0;
            for(Query sq : query.getQueries()) {
                String subq = qryStr.substring(sq.start+difference, sq.end+difference);
                Pair<ReturnResult, List<RevisionResult>> queryResults = performQuery(subq, sq);
                if(queryResults.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
                    return queryResults;
                }
                if(queryResults.getRight().isEmpty()) {
                    return new ImmutablePair<>(ReturnResult.EMPTY_SUBQUERY, null);
                }

                String replc = "(";
                for(RevisionResult rr : queryResults.getRight()) {
                    if(replc.length() > 1) {
                        replc += " ";
                    }
                    replc += rr.getId();
                }
                replc += ")";
                String newQry = qryStr.substring(0, sq.start+difference-2);
                newQry += replc;
                newQry += qryStr.substring(sq.end+difference+2);
                qryStr = newQry;

                difference += (replc.length()-4) - (sq.end-sq.start);
            }
        }
        return performSearch(qryStr);
    }

    private Pair<ReturnResult, List<RevisionResult>> performSearch(String qryStr) {
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
        List<RevisionResult> results = searcher.executeSearch(command).getResults();
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

    private static String pStart = ":S\\{";
    private static String pEnd = "}S(?=\\s|\\z)";

    private void findQueries(String qry, Query query) {
        Pattern p = Pattern.compile(pStart);

        Matcher starts = p.matcher(qry);
        p = Pattern.compile(pEnd);
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
                    Query subquery = new Query(rs, i);
                    query.queries.add(subquery);
                    findQueries(qry.substring(rs, i), subquery);
                }
            }
        }
    }

    private static class Query {
        private final int start;
        private final int end;
        private final List<Query> queries = new ArrayList<>();

        public Query(int start, int end) {
            this.start = start;
            this.end = end;
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
