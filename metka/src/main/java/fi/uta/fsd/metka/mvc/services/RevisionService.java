package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.search.RevisionSearch;
import fi.uta.fsd.metka.storage.repository.*;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains operations common for all revisions like save and approve.
 * If there are special restrictions on operation usage then it's done here.
 */
@Service
public class RevisionService {
    private static Logger logger = LoggerFactory.getLogger(RevisionService.class);

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
    private GeneralRepository general;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private IndexerComponent indexer;

    private final Map<RevisionKey, IndexerCommand> studyCommandBatch = new ConcurrentHashMap<>();

    private volatile boolean runningBatch = false;

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
        Pair<ReturnResult, RevisionableInfo> infoPair = general.getRevisionableInfo(dataPair.getRight().getKey().getId());
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
        Pair<ReturnResult, RevisionData> operationResult = create.create(request);
        if(operationResult.getLeft() == ReturnResult.REVISION_CREATED) {
            TransferData data = TransferData.buildFromRevisionData(operationResult.getRight(), RevisionableInfo.FALSE);
            addIndexCommand(data);
            return getResponse(new ImmutablePair<>(operationResult.getLeft(), data));
        } else {
            return getResponse(new ImmutablePair<ReturnResult, TransferData>(operationResult.getLeft(), null));
        }
    }

    public RevisionOperationResponse edit(TransferData transferData) {
        Pair<ReturnResult, RevisionData> operationResult = edit.edit(transferData);
        if(operationResult.getLeft() == ReturnResult.REVISION_CREATED) {
            TransferData data = TransferData.buildFromRevisionData(operationResult.getRight(), RevisionableInfo.FALSE);
            addIndexCommand(data);
            return getResponse(new ImmutablePair<>(operationResult.getLeft(), data));
        } else {
            return getResponse(new ImmutablePair<ReturnResult, TransferData>(operationResult.getLeft(), null));
        }
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

    public RevisionOperationResponse remove(TransferData transferData) {
        RevisionOperationResponse response = new RevisionOperationResponse();
        RemoveResult result = remove.remove(transferData);
        response.setResult(result.name());

        switch(result) {
            case FINAL_REVISION:
                // TODO: In case of study we should most likely remove all sub objects too since they won't point to anything.
                // TODO: We should also check that references will get removed from revisions (from which point is the question).
                //       For example if only revision of study attachment is removed then the row should be removed from study too.
            case SUCCESS_DRAFT:
                // One remove operation should be enough for both of these since there should only be one affected document
                addRemoveCommand(transferData);
                break;
            case SUCCESS_LOGICAL:
                // In this case we need to reindex all affected documents instead
                // TODO: Add indexing commands for all revisions of revisionable
                break;
            default:
                // Errors don't need special handling
                break;
        }

        return response;
    }

    public RevisionOperationResponse restore(TransferData transferData) {
        RevisionOperationResponse response = new RevisionOperationResponse();
        RemoveResult result = restore.restore(transferData);
        response.setResult(result.name());

        if(result == RemoveResult.SUCCESS_RESTORE) {
            // TODO: Reindex all revisions
        }

        return response;
    }

    private RevisionOperationResponse getResponse(Pair<ReturnResult, TransferData> pair) {
        RevisionOperationResponse response = new RevisionOperationResponse();
        response.setResult(pair.getLeft().name());
        response.setData(pair.getRight());
        return response;
    }

    public RevisionSearchResponse search(RevisionSearchRequest request) {
        RevisionSearchResponse response = new RevisionSearchResponse();

        Pair<ReturnResult, List<RevisionSearchResult>> result = search.search(request);
        response.setResult(result.getLeft());
        if(result.getLeft() == ReturnResult.SEARCH_SUCCESS) {
            for(RevisionSearchResult searchResult : result.getRight()) {
                response.getRows().add(searchResult);
            }
        }

        return response;
    }

    public RevisionSearchResponse studyIdSearch(String studyId) {
        RevisionSearchResponse response = new RevisionSearchResponse();
        Pair<ReturnResult, List<RevisionSearchResult>> result = search.studyIdSearch(studyId);
        response.setResult(result.getLeft());
        if(result.getLeft() == ReturnResult.SEARCH_SUCCESS && !result.getRight().isEmpty()) {
            response.getRows().add(result.getRight().get(0));
        }
        return response;
    }

    private void addRemoveCommand(TransferData data) {
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLE:
            case STUDY_VARIABLES: {
                // In these cases we need to reindex study/studies instead of removing something
                Long id = Long.parseLong(data.getField("study").getValue(Language.DEFAULT).getCurrent());
                addStudyIndexerCommand(id, true);
                break;
            }
            default:
                for(Language language : Language.values()) {
                    indexer.addCommand(
                            RevisionIndexerCommand
                                    .remove(data.getConfiguration().getType(), language, data.getKey().getId(), data.getKey().getNo()));
                }
                break;
        }
    }

    private void addIndexCommand(TransferData data) {
        // Separates calls to index sub components of study, should really be collected as a queue so that multiple study indexing requests are not made in a short period
        switch(data.getConfiguration().getType()) {
            case STUDY_ATTACHMENT:
            case STUDY_VARIABLE:
            case STUDY_VARIABLES: {
                Long id = Long.parseLong(data.getField("study").getValue(Language.DEFAULT).getCurrent());
                addStudyIndexerCommand(id, true);
                break;
            }
            default:
                // TODO: Make some way of indexing only changed languages instead of every one. For now add all languages. Indexing should also check to see that there is actual language specific data before adding to index
                for(Language language : Language.values()) {
                    indexer.addCommand(
                            RevisionIndexerCommand
                                    .index(data.getConfiguration().getType(), language, data.getKey().getId(), data.getKey().getNo()));
                }
                break;
        }
    }

    private void addStudyIndexerCommand(Long id, boolean index) {
        Pair<ReturnResult, RevisionData> pair = general.getLatestRevisionForIdAndType(id, false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Tried to add index command for study with id "+id+" but didn't find any revisions with result "+pair.getLeft());
            return;
        }
        try {
            // Wait while the current batch is being handled
            while(runningBatch = true) {
                Thread.sleep(500);
            }
            if(studyCommandBatch.containsKey(pair.getRight().getKey())) {
                return;
            }
            if(index) {
                studyCommandBatch.put(pair.getRight().getKey(), RevisionIndexerCommand.index(ConfigurationType.STUDY, pair.getRight().getKey()));
            } else {
                studyCommandBatch.put(pair.getRight().getKey(), RevisionIndexerCommand.remove(ConfigurationType.STUDY, pair.getRight().getKey()));
            }
        } catch(InterruptedException ie) {
            // Well damn, let's not add the index command then
        }
    }

    @Scheduled(fixedDelay = 1 * 60 *1000)
    private void executeStudyBatch() {
        runningBatch = true;
        for(IndexerCommand command : studyCommandBatch.values()) {
            indexer.addCommand(command);
        }
        studyCommandBatch.clear();
        runningBatch = false;
    }
}
