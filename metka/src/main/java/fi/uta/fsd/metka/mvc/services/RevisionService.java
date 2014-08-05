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
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private IndexerComponent indexer;

    private static Map<String, DirectoryManager.DirectoryPath> indexerPaths = new HashMap<>();

    static {
        // TODO: Add language paths
        for(ConfigurationType type : ConfigurationType.values()) {
            indexerPaths.put(type.toValue(), DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, "fi", type.toValue()));
        }
    }

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
        response.setResult(ReturnResult.VIEW_SUCCESSFUL);
        return response;
    }

    public RevisionOperationResponse create(RevisionCreateRequest request) {
        Pair<ReturnResult, TransferData> operationResult = create.create(request);
        if(operationResult.getLeft() == ReturnResult.REVISION_CREATED) {
            addIndexCommand(operationResult.getRight());
        }
        return getResponse(operationResult);
    }

    public RevisionOperationResponse edit(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = edit.edit(transferData);
        if(operationResult.getLeft() == ReturnResult.REVISION_CREATED) {
            addIndexCommand(operationResult.getRight());
        }
        return getResponse(operationResult);
    }

    public RevisionOperationResponse save(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData);
        if(operationResult.getLeft() == ReturnResult.SAVE_SUCCESSFUL_WITH_ERRORS || operationResult.getLeft() == ReturnResult.SAVE_SUCCESSFUL) {
            addIndexCommand(operationResult.getRight());
        }
        return getResponse(operationResult);
    }

    public RevisionOperationResponse approve(TransferData transferData) {
        Pair<ReturnResult, TransferData> operationResult = save.saveRevision(transferData);
        if(operationResult.getLeft() == ReturnResult.SAVE_SUCCESSFUL_WITH_ERRORS) {
            addIndexCommand(operationResult.getRight());
        }
        if(operationResult.getLeft() != ReturnResult.SAVE_SUCCESSFUL) {
            return getResponse(operationResult);
        }

        operationResult = approve.approve(operationResult.getRight());
        addIndexCommand(operationResult.getRight());
        return getResponse(operationResult);
    }

    private RevisionOperationResponse getResponse(Pair<ReturnResult, TransferData> pair) {
        RevisionOperationResponse response = new RevisionOperationResponse();
        response.setResult(pair.getLeft());
        response.setData(pair.getRight());
        return response;
    }

    public RevisionSearchResponse search(RevisionSearchRequest request) {
        RevisionSearchResponse response = new RevisionSearchResponse();

        // TODO: Call correct searches

        return response;
    }

    private void addIndexCommand(TransferData data) {
        // Separates calls to index sub components of study, should really be collected as a queue so that multiple study indexing requests are not made in a short period
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
                // TODO: Get study id and index that instead
                break;
            case STUDY_VARIABLE:
                // TODO: Get study id and index that instead
                break;
            case STUDY_VARIABLES:
                // TODO: Get study id and index that instead
                break;
            default:
                indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get(data.getConfiguration().getType().toValue()),
                        data.getKey().getId(), data.getKey().getNo()));
                break;
        }
    }

}
