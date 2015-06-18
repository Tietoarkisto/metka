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

package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.Check;
import fi.uta.fsd.metka.model.configuration.Condition;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

class QueryTargetHandler {

    static boolean handle(Target target, DataFieldContainer context, DataFieldValidator validator, Configuration configuration, SearcherComponent searcher) {
        ResultList<? extends SearchResult> result = performQuery(target.getContent(), searcher, context, configuration);
        for(Check check : target.getChecks()) {
            // Check is enabled
            if(validator.validate(check.getRestrictors(), context, configuration)) {
                if(!checkCondition(result, check.getCondition())) {
                    return false;
                }
            }
        }
        return validator.validate(target.getTargets(), context, configuration);
    }

    private static ResultList<? extends SearchResult> performQuery(String query, SearcherComponent searcher, DataFieldContainer context, Configuration configuration) {
        query = query.replace("{id}", context.getRevisionKey().getId().toString());
        try {
            return searcher.executeSearch(ExpertRevisionSearchCommand.build(query, configuration));
        } catch (QueryNodeException qne) {
            Logger.error(QueryTargetHandler.class, "Exception while performing query: " + query);
            return new ListBasedResultList<>(ResultList.ResultType.REVISION);
        }
    }

    private static boolean checkCondition(ResultList<? extends SearchResult> result, Condition condition) {
        switch(condition.getType()) {
            case NOT_EMPTY:
                return notEmpty(result);
            case IS_EMPTY:
                return isEmpty(result);
            case UNIQUE:
                return unique(result);
            default:
                return true;
        }
    }

    private static boolean notEmpty(ResultList<? extends SearchResult> results) {
        return !results.getResults().isEmpty();
    }

    private static boolean isEmpty(ResultList<? extends SearchResult> results) {
        return results.getResults().isEmpty();
    }

    private static boolean unique(ResultList<? extends SearchResult> results) {
        return results.getResults().size() == 1;
    }
}
