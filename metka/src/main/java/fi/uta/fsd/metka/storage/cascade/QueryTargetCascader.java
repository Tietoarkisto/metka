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

package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.util.StringUtils;

/**
 * Handles QUERY type targets.
 * Performs the query given in 'content' and cascades the operation to each revision found by the query.
 * Query can contain placeholders:
 *  {id}    - Context id is placed here
 *  {key}   - Context key is placed here in the form {id}-{no}
 */
class QueryTargetCascader {

    static boolean cascade(CascadeInstruction instruction, Target t, DataFieldContainer context,
            Configuration configuration, Cascader.RepositoryHolder repositories) {
        if(!StringUtils.hasText(t.getContent())) {
            // No content means no cascade and successful operation
            return true;
        }

        // TODO: Allow restrictions in cascade operations, this gives more control and is easy to do
        /*for(Check check : target.getChecks()) {
            // Check is enabled
            if(cascader.cascade(check.getRestrictors(), context, configuration)) {
                if(!checkConditionForField(field, d, check.getCondition(), context, configuration, searcher)) {
                    return false;
                }
            }
        }*/

        ResultList<RevisionResult> revisions = performQuery(t.getContent(), repositories.getSearcher(), context, configuration);

        for(RevisionResult revision : revisions.getResults()) {
            Pair<ReturnResult, RevisionData> dataPair = repositories.getRevisions().getRevisionData(revision.getId(), revision.getNo());
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            if(!OperationCascader.cascade(dataPair.getRight(), instruction, repositories)) {
                return false;
            }
        }
        return true;
    }

    private static ResultList<RevisionResult> performQuery(String query, SearcherComponent searcher, DataFieldContainer context, Configuration configuration) {
        query = query.replace("{id}", context.getRevisionKey().getId().toString());
        query = query.replace("{key}", context.getRevisionKey().getId().toString()+"-"+context.getRevisionKey().getNo().toString());
        try {
            return searcher.executeSearch(ExpertRevisionSearchCommand.build(query, configuration));
        } catch (QueryNodeException qne) {
            Logger.error(QueryTargetCascader.class, "Exception while performing query: " + query);
            return new ListBasedResultList<>(ResultList.ResultType.REVISION);
        }
    }
}
