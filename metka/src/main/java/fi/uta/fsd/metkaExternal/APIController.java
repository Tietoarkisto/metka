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

import codebook25.CodeBookDocument;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.ddi.MetkaXmlOptions;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.mvc.services.*;
import fi.uta.fsd.metka.storage.repository.APIRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.reference.ReferenceOption;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAmqp.payloads.AuditPayload;
import fi.uta.fsd.metkaExternal.requests.*;
import fi.uta.fsd.metkaExternal.responses.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "")
public class APIController {
    @Autowired
    private APIRepository api;

    @Autowired
    private ExpertSearchService search;

    @Autowired
    private SettingsService settings;

    @Autowired
    private StudyService studies;

    @Autowired
    private ReferenceService references;

    @Autowired
    private RevisionService revisions;

    @Autowired
    private Messenger messenger;

    @RequestMapping(value = "performQuery", method = RequestMethod.POST)
    public @ResponseBody APIPerformQueryResponse performQuery(@RequestBody APIPerformQueryRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIPerformQueryResponse.authFail();
        }

        try {
            ExpertSearchQueryResponse searchResponse = search.performQuery(request.getRequest());
            return APIPerformQueryResponse.success(searchResponse);
        } catch(Exception e) {
            Logger.error(getClass(), "Exception while performing API query request: ", e);
            return APIPerformQueryResponse.caughtException(e);
        }
    }

    @RequestMapping(value = "indexRevisions", method = RequestMethod.POST)
    public @ResponseBody APIIndexRevisionsResponse performQuery(@RequestBody APIIndexRevisionsRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIIndexRevisionsResponse.authFail();
        }
        boolean missing = false;
        for(RevisionKey key : request.getTargets()) {
            if(key.getId() == null || key.getNo() == null) {
                missing = true;
                continue;
            }
            settings.indexRevision(key);
        }

        return APIIndexRevisionsResponse.success(missing);
    }

    @RequestMapping(value = "exportDDI", method = RequestMethod.POST)
    public @ResponseBody APIExportDDIResponse exportDDI(@RequestBody APIExportDDIRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIExportDDIResponse.authFail();
        }

        if(request.getKey().getId() == null || request.getKey().getNo() == null) {
            return APIExportDDIResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }

        Pair<ReturnResult, CodeBookDocument> pair = studies.exportDDI(request.getKey(), request.getLanguage());

        return APIExportDDIResponse.success(pair.getLeft(), pair.getRight().xmlText(MetkaXmlOptions.DDI_EXPORT_XML_OPTIONS));
    }

    @RequestMapping(value = "importDDI", method = RequestMethod.POST)
    public @ResponseBody APIImportDDIResponse importDDI(@RequestBody APIImportDDIRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIImportDDIResponse.authFail();
        }

        if(request.getKey().getId() == null || request.getKey().getNo() == null || StringUtils.isBlank(request.getPath())) {
            return APIImportDDIResponse.success(ReturnResult.PARAMETERS_MISSING);
        }

        ReturnResult result = studies.importDDI(request.getKey(), request.getPath());

        return APIImportDDIResponse.success(result);
    }

    @RequestMapping(value = "collectReferenceOptions", method = RequestMethod.POST)
    public @ResponseBody APIReferencePathResponse collectReferenceOptions(@RequestBody APIReferencePathRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIReferencePathResponse.authFail();
        }

        if(request.getRequest() == null) {
            return APIReferencePathResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }

        List<ReferenceOption> options = references.collectReferenceOptions(request.getRequest());

        return APIReferencePathResponse.success(ReturnResult.OPERATION_SUCCESSFUL, options);
    }

    @RequestMapping(value = "getConfiguration", method = RequestMethod.POST)
    public @ResponseBody APIConfigurationReadResponse getConfiguration(@RequestBody APIConfigurationReadRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIConfigurationReadResponse.authFail();
        }

        if(request.getKey() == null) {
            return APIConfigurationReadResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }

        ConfigurationResponse response = revisions.getConfiguration(request.getKey());

        return APIConfigurationReadResponse.success(response.getResult(), response);
    }

    @RequestMapping(value = "exportRevision", method = RequestMethod.POST)
    public @ResponseBody APIExportRevisionResponse exportRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIExportRevisionResponse.authFail();
        }

        if(request.getKey() == null) {
            return APIExportRevisionResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }

        RevisionExportResponse response = revisions.exportRevision(request.getKey());

        return APIExportRevisionResponse.success(response.getResult(), response);
    }

    @RequestMapping(value = "viewRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse viewRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response;
        if(request.getKey().getNo() == null) {
            response = revisions.view(request.getKey().getId());
        } else {
            response = revisions.view(request.getKey().getId(), request.getKey().getNo());
        }

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "editRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse editRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.edit(request.getKey());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "restoreRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse restoreRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.restore(request.getKey());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "claimRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse claimRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.claimRevision(request.getKey());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "releaseRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse releaseRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.releaseRevision(request.getKey());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "saveRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse saveRevision(@RequestBody APITransferDataRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            messenger.sendAmqpMessage(messenger.FA_AUDIT, AuditPayload.deny("API-käyttäjä ["+request.getAuthentication()+"] yritti tallentaa revision ["+request.getTransferData().getKey().asCongregateKey()+"] ilman tarvittavia oikeuksia"));
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getTransferData() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.save(request.getTransferData());

        messenger.sendAmqpMessage(messenger.FA_AUDIT, AuditPayload.allow("API-käyttäjä ["+request.getAuthentication()+"] tallensi revision ["+request.getTransferData().getKey().asCongregateKey()+"]"));
        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "approveRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse approveRevision(@RequestBody APITransferDataRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getTransferData() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.approve(request.getTransferData());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "removeRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse removeRevision(@RequestBody APIRevisionKeyRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getKey() == null || request.getKey().getId() == null || request.getKey().getNo() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.remove(request.getKey(), null);

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }

    @RequestMapping(value = "createRevision", method = RequestMethod.POST)
    public @ResponseBody APIRevisionOperationResponse createRevision(@RequestBody APICreateRevisionRequest request) {
        // Authenticate using API key mechanism
        if(!ExternalUtil.authenticate(api, request.getAuthentication())) {
            return APIRevisionOperationResponse.authFail();
        }

        if(request.getRequest() == null) {
            return APIRevisionOperationResponse.success(ReturnResult.PARAMETERS_MISSING, null);
        }
        RevisionDataResponse response = revisions.create(request.getRequest());

        return APIRevisionOperationResponse.success(response.getResult().getResult(), response);
    }
}
