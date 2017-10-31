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

import fi.uta.fsd.metka.enums.*;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.model.transfer.TransferField;
import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.model.transfer.TransferValue;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.*;
import fi.uta.fsd.metka.storage.response.OperationResponse;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metka.transfer.revisionable.RevisionableLogicallyRemovedRequest;
import fi.uta.fsd.metka.transfer.revisionable.RevisionableLogicallyRemovedResponse;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Contains operations common for all revisions like save and approve.
 * If there are special restrictions on operation usage then it's done here.
 */
@Service
public class RevisionServiceImpl implements RevisionService {

    @Autowired
    private ExpertSearchService expertSearch;

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

    private RevisionDataResponse getResponseData(OperationResponse finalResult, RevisionData data, Boolean fullTransferData) {
        RevisionDataResponse response = getResponse(finalResult, null);
        if(data == null) {
            return response;
        }

        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(data.getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            response.setResult(OperationResponse.build(infoPair.getLeft()));
            return response;
        }
        TransferData transferData = TransferData.buildFromRevisionData(data, infoPair.getRight());
        if (fullTransferData) {
            fetchTransferDataReferences(transferData, null, true);
        }
        response.setData(transferData);
        return response;
    }

    private RevisionDataResponse getResponseConfiguration(OperationResponse finalResult, RevisionData data, Boolean fullTransferData) {
        RevisionDataResponse response = getResponseData(finalResult, data, fullTransferData);

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

    private RevisionDataResponse getResponseGUI(OperationResponse finalResult, RevisionData data, Boolean fullTransferData) {
        RevisionDataResponse response = getResponseConfiguration(finalResult, data, fullTransferData);

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
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight(), false);
    }

    @Override public RevisionDataResponse view(Long id, Integer no) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id, no);
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight(), false);
    }

    @Override public RevisionDataResponse fullView(Long id) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id.toString());
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight(), true);
    }

    @Override public RevisionDataResponse fullView(Long id, Integer no) {
        Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(id, no);
        return getResponseGUI(OperationResponse.build(dataPair.getRight() != null ? ReturnResult.VIEW_SUCCESSFUL.name() : dataPair.getLeft().name()), dataPair.getRight(), true);
    }

    private void fetchTransferDataReferences(TransferData data, List<String> fetchedRefs, Boolean root) {
        if (root){
            fetchedRefs = new LinkedList<>();
            fetchedRefs.add(data.getKey().getId().toString());
        }
        Pair<ReturnResult, Configuration> configPair = configurations.findConfiguration(data.getConfiguration());
        if (!configPair.getLeft().equals(ReturnResult.CONFIGURATION_FOUND)){
            return;
        }
        List<String> currList = fetchedRefs;
        for (TransferField field : data.getFields().values()){
            fetchedRefs = new LinkedList<>(currList);
            switch(field.getType()){
                case REFERENCECONTAINER:
                    handleFetchReferenceContainer(field, fetchedRefs);
                    break;
                case CONTAINER:
                    handleFetchContainer(field, fetchedRefs, configPair.getRight());
                    break;
                case VALUE:
                    if (configPair.getRight().getField(field.getKey()) == null) {
                        continue;
                    }
                    if(!configPair.getRight().getField(field.getKey()).getType().equals(FieldType.REFERENCE) && !configPair.getRight().getField(field.getKey()).getType().equals(FieldType.SELECTION)) {
                        continue;
                    }
                    if (configPair.getRight().getField(field.getKey()).getType().equals(FieldType.REFERENCE)) {
                        handleFetchField(field, fetchedRefs, configPair.getRight());
                    } else if (configPair.getRight().getField(field.getKey()).getType().equals(FieldType.SELECTION)){
                        handleSelectionField(field, fetchedRefs, configPair.getRight());
                    }
                    break;
            }
        }
    }


    private void handleFetchReferenceContainer(TransferField field, List<String> fetchedRefs){
        List<String> currList = fetchedRefs;
        for (List<TransferRow> langRows : field.getRows().values()){
            for (TransferRow row : langRows){
                fetchedRefs = new LinkedList<>(currList);
                if (!fetchedRefs.contains(row.getValue())){
                    fetchedRefs.add(row.getValue().split("-")[0]);
                    Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(row.getValue());
                    if (!revisionPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                        continue;
                    }
                    Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(row.getValue().split("-")[0]));
                    if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                        continue;
                    }
                    TransferData refData = TransferData.buildFromRevisionData(revisionPair.getRight(), infoPair.getRight());
                    fetchTransferDataReferences(refData, fetchedRefs, false);
                    row.setExtendedData(refData);
                }
            }
        }
    }

    private void handleFetchContainer(TransferField field, List<String> fetchedRefs, Configuration configuration){
        List<String> currList = fetchedRefs;
        for (List<TransferRow> langRows : field.getRows().values()){
            for (TransferRow row : langRows){
                for (TransferField rowField : row.getFields().values()){
                    fetchedRefs = new LinkedList<>(currList);
                    switch(rowField.getType()){
                        case REFERENCECONTAINER:
                            handleFetchReferenceContainer(rowField, fetchedRefs);
                            break;
                        case CONTAINER:
                            handleFetchContainer(rowField, fetchedRefs, configuration);
                            break;
                        case VALUE:
                            if (configuration.getField(field.getKey()) == null) {
                                continue;
                            }
                            if (!configuration.getField(field.getKey()).getType().equals(FieldType.REFERENCE) && !configuration.getField(field.getKey()).getType().equals(FieldType.SELECTION)){
                                continue;
                            }
                            if (configuration.getField(field.getKey()).getType().equals(FieldType.REFERENCE)) {
                                handleFetchField(field, fetchedRefs, configuration);
                            } else if (configuration.getField(field.getKey()).getType().equals(FieldType.SELECTION)){
                                handleSelectionField(field, fetchedRefs, configuration);
                            }
                            break;
                    }
                }
            }
        }
    }

    private void handleFetchField(TransferField field, List<String> fetchedRefs, Configuration configuration) {
        List<String> currList = fetchedRefs;
        switch(configuration.getReference(configuration.getField(field.getKey()).getReference()).getType()){
            case REVISION:
                for (TransferValue value : field.getValues().values()) {
                    fetchedRefs = new LinkedList<>(currList);
                    if (fetchedRefs.contains(value.getValue())){
                        continue;
                    }
                    fetchedRefs.add(value.getValue().split("-")[0]);
                    Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(value.getValue());
                    if (!revisionPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                        continue;
                    }
                    Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(value.getValue().split("-")[0]));
                    if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                        continue;
                    }
                    TransferData refData = TransferData.buildFromRevisionData(revisionPair.getRight(), infoPair.getRight());
                    fetchTransferDataReferences(refData, fetchedRefs, false);
                    value.setExtendedData(refData);
                }
                break;
            case REVISIONABLE:
                for (TransferValue value : field.getValues().values()){
                    fetchedRefs = new LinkedList<>(currList);
                    if (fetchedRefs.contains(value.getValue())){
                        continue;
                    }
                    fetchedRefs.add(value.getValue());
                    Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(value.getValue());
                    if (!revisionPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                        continue;
                    }
                    Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(value.getValue().split("-")[0]));
                    if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                        continue;
                    }
                    TransferData refData = TransferData.buildFromRevisionData(revisionPair.getRight(), infoPair.getRight());
                    fetchTransferDataReferences(refData, fetchedRefs, false);
                    value.setExtendedData(refData);
                }
                break;
        }
    }

    private void handleSelectionField(TransferField field, List<String> fetchedRefs, Configuration configuration) {
        List<String> currList = fetchedRefs;
        SelectionList list = configuration.getSelectionList(configuration.getField(field.getKey()).getSelectionList());
        if (!list.getType().equals(SelectionListType.REFERENCE)){
            return;
        }
        if (!configuration.getReference(list.getReference()).getType().equals(ReferenceType.REVISION) && !configuration.getReference(list.getReference()).getType().equals(ReferenceType.REVISIONABLE)){
            return;
        }
        for (TransferValue value : field.getValues().values()){
            fetchedRefs = new LinkedList<>(currList);
            if (fetchedRefs.contains(value.getValue()) ||!value.hasValue()){
                continue;
            }
            fetchedRefs.add(value.getValue().split("-")[0]);
            Pair<ReturnResult, RevisionData> revisionPair = revisions.getRevisionData(value.getValue().split("-")[0]);
            if (!revisionPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
                continue;
            }
            Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(Long.parseLong(value.getValue().split("-")[0]));
            if (!infoPair.getLeft().equals(ReturnResult.REVISIONABLE_FOUND)){
                continue;
            }
            TransferData refData = TransferData.buildFromRevisionData(revisionPair.getRight(), infoPair.getRight());
            fetchTransferDataReferences(refData, fetchedRefs, false);
            value.setExtendedData(refData);
        }
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
        return getResponseGUI(operationResult.getLeft(), operationResult.getRight(), false);
    }

    @Override public RevisionDataResponse save(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData, null);
        return getResponse(OperationResponse.build(operationResult.getLeft()), operationResult.getRight());
    }

    @Override public RevisionDataResponse createAndSave(TransferData transferData) {
        if (transferData.getKey() == null || transferData.getKey().getId() == null){
            RevisionCreateRequest createRequest = new RevisionCreateRequest();
            for (Map.Entry<String, TransferField> entry : transferData.getFields().entrySet()){
                // Mandatory values for creating revisions are always of the type "VALUE"
                if(entry.getValue().getType().equals(TransferFieldType.VALUE))
                    createRequest.getParameters().put(entry.getKey(), entry.getValue().getValueFor(Language.DEFAULT).getValue());
            }
            Pair<ReturnResult, RevisionData> dataPair = revisions.getRevisionData(transferData.getField("study").getValueFor(Language.DEFAULT).getValue());
            if (!AuthenticationUtil.isHandler(dataPair.getRight())){
                return getResponse(OperationResponse.build(ReturnResult.WRONG_USER), transferData);
            }
            createRequest.setType(transferData.getConfiguration().getType());
            Pair<ReturnResult, RevisionData> createOperationResult = create.create(createRequest);
            if (!createOperationResult.getLeft().equals(ReturnResult.REVISION_CREATED)){
                return getResponse(OperationResponse.build(createOperationResult.getLeft()), transferData);
            }
            claimRevision(createOperationResult.getRight().getKey());
            TransferData newData = new TransferData(createOperationResult.getRight().getKey(), transferData.getConfiguration());
            newData.getFields().putAll(transferData.getFields());
            transferData = newData;
        }
        Pair<ReturnResult, TransferData> saveOperationResult = save.saveRevision(transferData, null);
        return getResponse(OperationResponse.build(saveOperationResult.getLeft()), saveOperationResult.getRight());
    }

    @Override public MassRevisionDataResponse massCreateFiles(List<TransferData> transferDatas){
        MassRevisionDataResponse response = new MassRevisionDataResponse();
        for (TransferData transferData: transferDatas){
            RevisionDataResponse revisionDataResponse = createAndSave(transferData);
            if (response.getData() == null) {
                response.setConfiguration(revisionDataResponse.getConfiguration());
                response.setGui(revisionDataResponse.getGui());
                response.setResult(revisionDataResponse.getResult());
                response.setData(new LinkedList<TransferData>());
                response.getData().add(revisionDataResponse.getData());
            } else {
                response.getData().add(revisionDataResponse.getData());
                if (!revisionDataResponse.getResult().equals(ReturnResult.OPERATION_SUCCESSFUL)){
                    response.setResult(revisionDataResponse.getResult());
                }
            }
        }
        return response;
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

    /**
     * Creates a new revision for a revisionable and copies the fields of the target revision to it,
     * effectively "reverting" it.
     * @param key
     * @param targetRevision
     * @return
     */
    @Override public RevisionDataResponse revert(RevisionKey key, Integer targetRevision){
        Pair<OperationResponse, RevisionData> newPair = edit.edit(key, null);
        if (!newPair.getLeft().getResult().equals("REVISION_CREATED")){
            return getResponseGUI(newPair.getLeft(), newPair.getRight(), false);
        }
        Pair<ReturnResult, RevisionData> targetPair = revisions.getRevisionData(key.getId(), targetRevision);
        if (!targetPair.getLeft().equals(ReturnResult.REVISION_FOUND)){
            return getResponseGUI(newPair.getLeft(), newPair.getRight(), false);
        }

        Pair<ReturnResult, RevisionableInfo> newInfo = revisions.getRevisionableInfo(newPair.getRight().getKey().getId());
        Pair<ReturnResult, RevisionableInfo> targetInfo = revisions.getRevisionableInfo(newPair.getRight().getKey().getId());
        if (!newInfo.getLeft().equals(ReturnResult.REVISIONABLE_FOUND) && !targetInfo.getLeft().equals(ReturnResult.REVISIONABLE_FOUND))
            return getResponseGUI(newPair.getLeft(), newPair.getRight(), false);
        Pair<ReturnResult, TransferData> result = save.copyAndSaveRevision(TransferData.buildFromRevisionData(targetPair.getRight(), targetInfo.getRight()), TransferData.buildFromRevisionData(newPair.getRight(), newInfo.getRight()), null);
        return getResponseGUI(newPair.getLeft(), newPair.getRight(), false);
    }

    @Override public ExpertSearchQueryResponse search(RevisionSearchRequest request) throws Exception {
        List<String> qrys = new ArrayList<>();

        qrys.add(((!request.isSearchApproved())?"+":"")+"state.approved:"+request.isSearchApproved());
        qrys.add(((!request.isSearchDraft())?"+":"")+"state.draft:"+request.isSearchDraft());
        qrys.add(((!request.isSearchRemoved())?"+":"")+"state.removed:"+request.isSearchRemoved());
        qrys.add("+(state.latest:draft OR state.latest:approved)");

        for(String key : request.getValues().keySet()) {
            if(!StringUtils.hasText(request.getByKey(key))) {
                continue;
            }
            qrys.add("+"+key+":"+request.getByKey(key));
        }

        String qryStr = StringUtils.collectionToDelimitedString(qrys, " ");

        ExpertSearchQueryRequest expertSearchRequest = new ExpertSearchQueryRequest();
        expertSearchRequest.setQuery(qryStr);
        ExpertSearchQueryResponse response = expertSearch.performQuery(expertSearchRequest);
        return response;
    }

    @Override
    public RevisionDataResponse claimRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(key, false);
        return getResponse(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
    }

    @Override
    public RevisionDataResponse beginEditingRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.beginEditing(key);
        return getResponse(OperationResponse.build(dataPair.getLeft()), dataPair.getRight());
    }

    @Override
    public RevisionDataResponse releaseRevision(RevisionKey key) {
        Pair<ReturnResult, TransferData> dataPair = handler.changeHandler(key, true);
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
        return getResponseData(OperationResponse.build(pair.getRight() != null ? ReturnResult.REVISION_FOUND.name() : pair.getLeft().name()), pair.getRight(), false);
    }

    @Override
    public RevisionExportResponse exportRevision(RevisionKey key) {
        Pair<ReturnResult, RevisionableInfo> infoPair = revisions.getRevisionableInfo(key.getId());
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        RevisionExportResponse response = new RevisionExportResponse();
        response.setResult(pair.getLeft());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND && infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND ) {
            // Operation was not successful
            return response;
        }
        TransferData transferData = TransferData.buildFromRevisionData(pair.getRight(), infoPair.getRight());
        fetchTransferDataReferences(transferData, null, true);
        Pair<SerializationResults, String> result = json.serialize(transferData);
        if(result.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
            response.setResult(ReturnResult.OPERATION_FAIL);
            return response;
        }
        response.setContent(result.getRight());
        response.setKey(key);
        return response;
    }

    @Override
    public RevisionableLogicallyRemovedResponse revisionablesLogicallyRemoved(RevisionableLogicallyRemovedRequest request) {

        RevisionableLogicallyRemovedResponse response = new RevisionableLogicallyRemovedResponse();
        try {
            List<Long> ids = new ArrayList<>();
            for(String key : request.getValues()) {
                ids.add(Long.parseLong(key.split("-")[0]));
            }
            if (ids.size() == 0) {
                response.setResult(ReturnResult.PARAMETERS_MISSING);
                return response;
            }
            response.getValues().addAll(revisions.getRevisionablesLogicallyRemoved(ids));
            response.setResult(ReturnResult.OPERATION_SUCCESSFUL);
        } catch (NumberFormatException nfe) {
            response.setResult(ReturnResult.PARAMETERS_MISSING);
        }
        return response;
    }
}
