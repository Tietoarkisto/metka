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

package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "")
public class APIController {
    @Autowired
    private APIRepository api;

    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private IndexerComponent indexer;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    // TODO: implement request forwarding to needed services

    /*@RequestMapping(value = "createStudy", method = RequestMethod.POST)
    public @ResponseBody
    APIStudyCreateResponse createStudy(@RequestBody APIStudyCreateRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_STUDY_CREATE)) {
            APIStudyCreateResponse result = new APIStudyCreateResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return result;
        }

        RevisionCreateRequest revReq = new RevisionCreateRequest();
        revReq.setType(ConfigurationType.STUDY);
        revReq.getParameters().putAll(request.getParameters());
        Pair<ReturnResult, RevisionData> result = create.create(revReq);
        if(result.getLeft() != ReturnResult.REVISION_CREATED) {
            return new APIStudyCreateResponse(result.getLeft(), null);
        }

        Pair<StatusCode, ValueDataField> pair = result.getRight().dataField(ValueDataFieldCall.get("studyid"));
        if(pair.getLeft() != StatusCode.FIELD_FOUND || !pair.getRight().hasValueFor(Language.DEFAULT)) {
            return new APIStudyCreateResponse(ReturnResult.PARAMETERS_MISSING, null);
        }

        revisions.indexRevision(result.getRight().getKey());

        return new APIStudyCreateResponse(ReturnResult.REVISION_CREATED, pair.getRight().getActualValueFor(Language.DEFAULT));
    }

    @RequestMapping(value = "getConfiguration", method = RequestMethod.POST)
    public @ResponseBody
    APIConfigurationReadResponse getConfiguration(@RequestBody APIConfigurationReadRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_READ)) {
            APIConfigurationReadResponse response = new APIConfigurationReadResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return response;
        }
        Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(request.getKey());
        return new APIConfigurationReadResponse(pair.getLeft(), pair.getRight());
    }

    @RequestMapping(value = "getRevision", method = RequestMethod.GET)
    public @ResponseBody
    APIRevisionReadResponse getData(@RequestBody APIRevisionReadRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_READ)) {
            APIRevisionReadResponse response = new APIRevisionReadResponse(ReturnResult.API_AUTHENTICATION_FAILED, null);
            return response;
        }
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(RevisionKey.fromModelKey(request.getKey()));
        APIRevisionReadResponse response = new APIRevisionReadResponse(dataPair.getLeft(), dataPair.getRight());
        return response;
    }

    @RequestMapping(value = "index", method = RequestMethod.POST)
    public @ResponseBody ReturnResult indexRevisions(@RequestBody APIMassIndexRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_SEARCH)) {
            return ReturnResult.API_AUTHENTICATION_FAILED;
        }
        for(IndexTarget target : request.getTargets()) {
            revisions.indexRevision(target.getKey());
        }
        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public @ResponseBody ReturnResult saveData(@RequestBody APIRevisionSaveRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_EDIT)) {
            return ReturnResult.API_AUTHENTICATION_FAILED;
        }
        return revisions.updateRevisionData(request.getRevision());
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public @ResponseBody
    RevisionSearchResponse performSearch(@RequestBody APISearchRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication(), ExternalUtil.FLAG_SEARCH)) {
            RevisionSearchResponse response = new RevisionSearchResponse();
            response.setResult(ReturnResult.API_AUTHENTICATION_FAILED);
            return response;
        }
        try {
            System.err.println(AuthenticationUtil.getUserName());
            ExpertRevisionSearchCommand command = ExpertRevisionSearchCommand.build(request.getQuery(), configurations);
            ResultList<RevisionResult> results = searcher.executeSearch(command);
            RevisionSearchResponse response = new RevisionSearchResponse();
            if(results.getResults().isEmpty()) {
                response.setResult(ReturnResult.NO_RESULTS);
                return response;
            }
            response.setResult(ReturnResult.OPERATION_SUCCESSFUL);
            for(RevisionResult revResult : results.getResults()) {
                Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(revResult.getId());
                if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
                    // No revisionable so no actual data, continue
                    continue;
                }
                Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(revResult.getId(), revResult.getNo().intValue());
                if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                    // No actual revision so no data, continue
                    continue;
                }
                RevisionSearchResult result = RevisionSearchResult.build(dataPair.getRight(), infoPair.getRight());
                response.getRows().add(result);
            }
            return response;
        } catch(QueryNodeException qne) {
            RevisionSearchResponse response = new RevisionSearchResponse();
            response.setResult(ReturnResult.MALFORMED_QUERY);
            return response;
        }
    }*/
}
