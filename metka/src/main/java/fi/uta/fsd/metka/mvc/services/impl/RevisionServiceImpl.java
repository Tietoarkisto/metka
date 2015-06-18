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
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
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

    private RevisionDataResponse getResponse(Pair<ReturnResult, TransferData> dataPair) {
        RevisionDataResponse response = new RevisionDataResponse();
        fillResponseData(response, dataPair);
        return response;
    }

    private void fillResponseData(RevisionDataResponse response, Pair<ReturnResult, TransferData> dataPair) {
        response.setResult(dataPair.getLeft().name());
        response.setData(dataPair.getRight());
    }

    private void fillResponseData(String finalResult, RevisionDataResponse response, Pair<ReturnResult, RevisionData> dataPair) {
        if(dataPair.getRight() == null) {
            response.setResult(dataPair.getLeft().name());
            return;
        }
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(dataPair.getRight().getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            response.setResult(infoPair.getLeft().name());
        }
        response.setData(TransferData.buildFromRevisionData(dataPair.getRight(), infoPair.getRight()));
        response.setResult(finalResult);
    }

    private void fillResponseConfiguration(String finalResult, RevisionDataResponse response, Pair<ReturnResult, RevisionData> dataPair) {
        fillResponseData(finalResult, response, dataPair);

        Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(dataPair.getRight().getConfiguration());
        if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(configurationPair.getLeft().name());
            return;
        }
        response.setConfiguration(configurationPair.getRight());

        response.setResult(finalResult);
    }

    private void fillResponseGUI(String finalResult, RevisionDataResponse response, Pair<ReturnResult, RevisionData> dataPair) {
        fillResponseConfiguration(finalResult, response, dataPair);

        Pair<ReturnResult, GUIConfiguration> guiPair = configurations.findLatestGUIConfiguration(dataPair.getRight().getConfiguration().getType());
        if(guiPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(guiPair.getLeft().name());
            return;
        }
        response.setGui(guiPair.getRight());

        response.setResult(finalResult);
    }

    @Override public RevisionDataResponse view(Long id, ConfigurationType type) {
        RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(id, false, type);
        fillResponseGUI(ReturnResult.VIEW_SUCCESSFUL.name(), response, dataPair);
        return response;
    }

    @Override public RevisionDataResponse view(Long id, Integer no, ConfigurationType type) {
        RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionDataOfType(id, no, type);
        fillResponseGUI(ReturnResult.VIEW_SUCCESSFUL.name(), response, dataPair);
        return response;
    }

    @Override public RevisionDataResponse create(RevisionCreateRequest request) {
        //RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> operationResult = create.create(request);
        //fillResponseData(ReturnResult.REVISION_CREATED.name(), response, operationResult);
        RevisionDataResponse response = getResponse(new ImmutablePair<>(operationResult.getLeft(), TransferData.buildFromRevisionData(operationResult.getRight(), RevisionableInfo.FALSE)));
        return response;
    }

    @Override public RevisionDataResponse edit(TransferData transferData) {
        RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> operationResult = edit.edit(transferData, null);
        fillResponseGUI(operationResult.getLeft().name(), response, operationResult);
        return response;
    }

    @Override public RevisionDataResponse save(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData, null);

        return getResponse(operationResult);
    }

    @Override public RevisionDataResponse approve(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData, null);
        if(operationResult.getLeft() == ReturnResult.OPERATION_SUCCESSFUL || operationResult.getLeft() == ReturnResult.NO_CHANGES) {
            operationResult = approve.approve(operationResult.getRight(), null);
        }
        return getResponse(operationResult);
    }

    @Override public RevisionDataResponse remove(TransferData transferData, Boolean draft) {
        RevisionDataResponse response = new RevisionDataResponse();
        RemoveResult result;
        if(draft == null) {
            result = remove.remove(transferData, null);
        } else if(draft) {
            result = remove.removeDraft(transferData, null);
        } else {
            result = remove.removeLogical(transferData, null);
        }
        response.setResult(result.name());

        return response;
    }

    @Override public RevisionDataResponse restore(RevisionKey key) {
        RevisionDataResponse response = new RevisionDataResponse();
        RemoveResult result = restore.restore(key.getId());
        response.setResult(result.name());

        response.setData(TransferData.buildFromRevisionData(revisions.getLatestRevisionForIdAndType(key.getId(), false, null).getRight(), RevisionableInfo.FALSE));

        return response;
    }

    @Override public RevisionSearchResponse search(RevisionSearchRequest request) {
        RevisionSearchResponse response = new RevisionSearchResponse();

        Pair<ReturnResult, List<RevisionSearchResult>> result = search.search(request);
        response.setResult(result.getLeft());
        if(result.getLeft() == ReturnResult.OPERATION_SUCCESSFUL) {
            for(RevisionSearchResult searchResult : result.getRight()) {
                response.getRows().add(searchResult);
            }
        }

        return response;
    }

    @Override
    public RevisionDataResponse claimRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key), false);
        return getResponse(dataPair);
    }

    @Override
    public RevisionDataResponse releaseRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key), true);
        return getResponse(dataPair);
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
    public RevisionDataResponse adjacentRevision(AdjacentRevisionRequest request) {
        RevisionDataResponse response = new RevisionDataResponse();
        Pair<ReturnResult, RevisionData> pair = revisions.getAdjacentRevision(request);
        fillResponseData(ReturnResult.REVISION_FOUND.name(), response, pair);
        return response;
    }

    @Override
    public RevisionExportResponse exportRevision(TransferData transferData) {

        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(transferData.getKey()));
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
        response.setId(transferData.getKey().getId());
        response.setNo(transferData.getKey().getNo());
        return response;
    }
}
