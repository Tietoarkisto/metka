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

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.*;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains operations common for all revisions like save and approve.
 * If there are special restrictions on operation usage then it's done here.
 */
@Service
public class RevisionServiceImpl implements RevisionService {

    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionApproveRepository approve;

    @Autowired
    private RevisionRemoveRepository remove;

    @Autowired
    private RevisionRestoreRepository restore;

    @Autowired
    private RevisionSearch search;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionHandlerRepository handler;

    @Autowired
    private JSONUtil json;

    private RevisionDataResponse getResponse(OperationResponse result, TransferData data) {
        RevisionDataResponse response = new RevisionDataResponse();
        response.setResult(result);
        response.setData(data);
        return response;
    }

    private RevisionDataResponse getResponseData(OperationResponse finalResult, RevisionData data) {
        RevisionDataResponse response = getResponse(finalResult, null);

        if(data == null) {
            return response;
        }

        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            response.setResult(OperationResponse.build(infoPair.getLeft()));
            return response;
        }

        response.setData(TransferData.buildFromRevisionData(data, infoPair.getRight()));
        return response;
    }

    private RevisionDataResponse getResponseConfiguration(OperationResponse finalResult, RevisionData data) {
        RevisionDataResponse response = getResponseData(finalResult, data);

        if(data == null) {
            return response;
        }

        Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(data.getConfiguration());
        if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(OperationResponse.build(configurationPair.getLeft()));
            return response;
        }

        response.setConfiguration(configurationPair.getRight());
        return response;
    }

    private RevisionDataResponse getResponseGUI(OperationResponse finalResult, RevisionData data) {
        RevisionDataResponse response = getResponseConfiguration(finalResult, data);

        if(data == null) {
            return response;
        }

        Pair<ReturnResult, GUIConfiguration> guiPair = configurations.findLatestGUIConfiguration(data.getConfiguration().getType());
        if(guiPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(OperationResponse.build(guiPair.getLeft()));
            return response;
        }

        response.setGui(guiPair.getRight());
        return response;
    }

    @Override public RevisionDataResponse view(Long id) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id.toString());
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight());
    }

    @Override public RevisionDataResponse view(Long id, Integer no) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id, no);
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight());
    }

    @Override public RevisionDataResponse create(RevisionCreateRequest request) {
        //RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> operationResult = create.create(request);
        //fillResponseData(ReturnResult.REVISION_CREATED.name(), response, operationResult);
        return getResponse(OperationResponse.build(operationResult.getLeft()),
                operationResult.getRight() != null ? TransferData.buildFromRevisionData(operationResult.getRight(), RevisionableInfo.FALSE) : null);
    }

    @Override public RevisionDataResponse edit(RevisionKey key) {
        Pair<OperationResponse, RevisionData> operationResult = edit.edit(key, null);
        return getResponseGUI(operationResult.getLeft(), operationResult.getRight());
    }

    @Override public RevisionDataResponse save(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData, null);
        return getResponse(OperationResponse.build(operationResult.getLeft()), operationResult.getRight());
    }

    @Override public RevisionDataResponse approve(TransferData transferData) {
        Pair<ReturnResult, TransferData> savePair = save.saveRevision(transferData, null);
        String result = savePair.getLeft().name();
        TransferData data = savePair.getRight();
        if(savePair.getLeft() == ReturnResult.OPERATION_SUCCESSFUL || savePair.getLeft() == ReturnResult.NO_CHANGES) {
            Pair<OperationResponse, TransferData> response = approve.approve(savePair.getRight(), null);
            return getResponse(response.getLeft(), response.getRight());
        } else {
            return getResponse(OperationResponse.build(savePair.getLeft()), savePair.getRight());
        }
    }

    @Override public RevisionDataResponse remove(RevisionKey key, Boolean draft) {
        RevisionDataResponse response = new RevisionDataResponse();
        OperationResponse result;
        if(draft == null) {
            result = remove.remove(key, null);
        } else if(draft) {
            result = remove.removeDraft(key, null);
        } else {
            result = remove.removeLogical(key, null, false);
        }
        response.setResult(result);

        return response;
    }

    @Override public RevisionDataResponse restore(RevisionKey key) {
        RevisionDataResponse response = new RevisionDataResponse();
        RemoveResult result = restore.restore(key.getId());
        response.setResult(OperationResponse.build(result));

        response.setData(TransferData.buildFromRevisionData(revisions.getRevisionData(key.getId().toString()).getRight(), RevisionableInfo.FALSE));

        return response;
    }

    @Override public RevisionSearchResponse search(RevisionSearchRequest request) {
        RevisionSearchResponse response = new RevisionSearchResponse();

        Pair<ReturnResult, ResultList<RevisionResult>> result = search.search(request);
        response.setResult(result.getLeft());
        response.setResults(result.getRight());

        return response;
    }

    @Override
    public RevisionDataResponse claimRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key), false);
        return getResponse(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
    }

    @Override
    public RevisionDataResponse beginEditingRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.beginEditing(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
        return getResponse(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
    }

    @Override
    public RevisionDataResponse releaseRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key), true);
        return getResponse(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
    }

    @Override
    public RevisionSearchResponse collectRevisionHistory(RevisionHistoryRequest request) {
        RevisionSearchResponse response = new RevisionSearchResponse();

        Pair<ReturnResult, List<RevisionSearchResult>> results = search.collectRevisionHistory(request);
        response.setResult(results.getLeft());
        if(results.getLeft() == ReturnResult.OPERATION_SUCCESSFUL) {
            response.getRows().addAll(results.getRight());
        }
        return response;
    }

    @Override
    public RevisionCompareResponse revisionCompare(RevisionCompareRequest request) {
        List<RevisionCompareResponseRow> rows = search.compareRevisions(request);
        RevisionCompareResponse response = new RevisionCompareResponse(rows.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL);
        response.getRows().addAll(rows);
        return response;
    }

    @Override
    public ConfigurationResponse getConfiguration(ConfigurationType type) {
        Pair<ReturnResult, Configuration> pair = configurations.findLatestConfiguration(type);
        return new ConfigurationResponse(pair.getLeft(), pair.getRight());
    }

    @Override
    public ConfigurationResponse getConfiguration(ConfigurationKey key) {
        Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(key);
        return new ConfigurationResponse(pair.getLeft(), pair.getRight());
    }

    @Override
    public RevisionDataResponse adjacentRevision(AdjacentRevisionRequest request) {
        Pair<ReturnResult, RevisionData> pair = revisions.getAdjacentRevision(request);
        return getResponseData(OperationResponse.build(pair.getRight() != null ? ReturnResult.REVISION_FOUND.name() : pair.getLeft().name()), pair.getRight());
    }

    @Override
    public RevisionExportResponse exportRevision(RevisionKey key) {

        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
        RevisionExportResponse response = new RevisionExportResponse();
        response.setResult(pair.getLeft());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // Operation was not successful
            return response;
        }
        Pair<SerializationResults, String> result = json.serialize(pair.getRight());
        if(result.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
            response.setResult(ReturnResult.OPERATION_FAIL);
            return response;
        }
        response.setContent(result.getRight());
        response.setKey(key);
        return response;
    }
}
