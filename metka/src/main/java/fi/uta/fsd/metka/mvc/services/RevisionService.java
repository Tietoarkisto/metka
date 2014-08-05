package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RemovedInfo;
import fi.uta.fsd.metka.transfer.revision.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Contains operations common for all revisions like save and approve.
 * If there are special restrictions on operation usage then it's done here.
 */
@Service
public class RevisionService {
    @Autowired
    private RevisionCreationRepository create;

    @Autowired
    private RevisionEditRepository edit;

    @Autowired
    private RevisionSaveRepository save;

    @Autowired
    private RevisionApproveRepository approve;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private ConfigurationRepository configurations;

    // TODO: Add indexer requests

    public RevisionDataResponse view(Long id, String type) {
        RevisionDataResponse response = new RevisionDataResponse();
        if(!ConfigurationType.isValue(type)) {
            response.setResult(ReturnResult.TYPE_NOT_VALID_CONFIGURATION_TYPE);
            return response;
        }
        Pair<ReturnResult, RevisionData> dataPair = general.getLatestRevisionForIdAndType(id, false, ConfigurationType.fromValue(type));
        return fillResponse(response, dataPair);
    }

    public RevisionDataResponse view(Long id, Integer no, String type) {
        RevisionDataResponse response = new RevisionDataResponse();
        if(!ConfigurationType.isValue(type)) {
            response.setResult(ReturnResult.TYPE_NOT_VALID_CONFIGURATION_TYPE);
            return response;
        }
        Pair<ReturnResult, RevisionData> dataPair = general.getRevisionDataOfType(id, no, ConfigurationType.fromValue(type));
        return fillResponse(response, dataPair);
    }

    private RevisionDataResponse fillResponse(RevisionDataResponse response, Pair<ReturnResult, RevisionData> dataPair) {
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            response.setResult(dataPair.getLeft());
            return response;
        }
        Pair<ReturnResult, RemovedInfo> infoPair = general.getRevisionableRemovedInfo(dataPair.getRight().getKey().getId());
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            response.setResult(infoPair.getLeft());
        }
        response.setTransferData(TransferData.buildFromRevisionData(dataPair.getRight(), infoPair.getRight()));

        Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(dataPair.getRight().getConfiguration());
        if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(configurationPair.getLeft());
            return response;
        }
        response.setConfiguration(configurationPair.getRight());

        Pair<ReturnResult, GUIConfiguration> guiPair = configurations.findLatestGUIConfiguration(configurationPair.getRight().getKey().getType());
        if(guiPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            response.setResult(guiPair.getLeft());
            return response;
        }
        response.setGui(guiPair.getRight());
        return response;
    }

    public RevisionOperationResponse create(RevisionCreateRequest request) {
        Pair<ReturnResult, TransferData> operationResult = create.create(request);
        return getResponse(operationResult);
    }

    public RevisionOperationResponse edit(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = edit.edit(transferData);
        return getResponse(operationResult);
    }

    public RevisionOperationResponse save(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData);
        return getResponse(operationResult);
    }

    public RevisionOperationResponse approve(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData);
        if(operationResult.getLeft() != ReturnResult.SAVE_SUCCESSFUL) {
            return getResponse(operationResult);
        }

        operationResult = approve.approve(operationResult.getRight());
        return getResponse(operationResult);
    }

    private RevisionOperationResponse getResponse(Pair<ReturnResult, TransferData> pair) {
        RevisionOperationResponse response = new RevisionOperationResponse();
        response.setResult(pair.getLeft());
        response.setData(pair.getRight());
        return response;
    }

    public RevisionSearchResponse search(RevisionSearchRequest request, String type) {
        RevisionSearchResponse response = new RevisionSearchResponse();
        if(!ConfigurationType.isValue(type)) {
            response.setResult(ReturnResult.TYPE_NOT_VALID_CONFIGURATION_TYPE);
            return response;
        }

        return response;
    }

}
