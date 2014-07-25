package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.storage.repository.StudyRepository;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.services.simple.transfer.SearchResult;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import fi.uta.fsd.metka.mvc.services.simple.study.StudySearchSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudyService {
    @Autowired
    private StudyRepository repository;
    @Autowired
    private GeneralSearch generalSearch;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private IndexerComponent indexer; // Provide revision indexing services.

    private static Map<String, DirectoryManager.DirectoryPath> indexerPaths = new HashMap<>();

    static {
        indexerPaths.put("fi", DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, "fi", ConfigurationType.STUDY.toValue()));
    }

    public List<SearchResult> searchForStudies(StudySearchSO query) {
        List<SearchResult> resultList = new ArrayList<>();
        // TODO: Find searched studies and create search result objects for them.

        // For now return all studies with their latest approved and draft revisions
        List<RevisionDataRemovedContainer> datas = null;
        try {
            datas = generalSearch.tempFindAllStudies();
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return resultList;
        }

        for(RevisionDataRemovedContainer container : datas) {
            SearchResult study = resultSOFromRevisionData(container.getData());
            if(study != null) {
                if(container.isRemoved()) {
                    study.setState(UIRevisionState.REMOVED);
                }
                resultList.add(study);
            }
        }



        return resultList;
    }

    // Helper functions
    private SearchResult resultSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.STUDY) {
            return null;
        }

        SearchResult so = new SearchResult();
        so.setId(data.getKey().getId());
        so.setRevision(data.getKey().getRevision());
        so.setState(UIRevisionState.fromRevisionState(data.getState()));
        so.setByKey("studyid", SavedDataField.valueAsInteger(data.dataField(SavedDataFieldCall.get("studyid")).getRight()));
        so.setByKey("title", SavedDataField.valueAsString(data.dataField(SavedDataFieldCall.get("title")).getRight()));

        return so;
    }

    /**
     * Return a default revision number for requested revisionable
     * @param id Revisionable id
     * @return
     */
    public Integer findSingleRevisionNo(Long id) {
        Integer revision = generalSearch.findSingleRevisionNo(id);
        return revision;
    }

    /**
     * Find requested revision data and convert it to SeriesSingle simple object
     * @param id Revisionable id
     * @param revision Revision number
     * @return Revision data converted to TransferObject
     */
    public RevisionViewDataContainer findSingleRevision(Long id, Integer revision) {
        RevisionData data = null;

        // Check for FileLinkQueue events.
        // If given id/revision belongs to a draft revision then process possible FileLinkQueue events.
        // This makes sure that any recently added file references are found from their respective REFERENCECONTAINERs.
        // Also if there is a new POR file present then that is parsed and the data is added
        try {
            repository.checkFileLinkQueue(id, revision);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
        }

        try {
            data = generalSearch.findSingleRevision(id, revision, ConfigurationType.STUDY);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        if(data == null) {
            return null;
        }

        Configuration config = configService.findByTypeAndVersion(data.getConfiguration());
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);

        return new RevisionViewDataContainer(single, config);
    }

    public RevisionViewDataContainer newStudy(Long acquisition_number) {
        RevisionData revision;
        try {
            revision = repository.getNew(acquisition_number);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        // Creating new series was successful, index series
        try {
            indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), revision.getKey().getId(), revision.getKey().getRevision()));
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }

        Configuration config = configService.findByTypeAndVersion(revision.getConfiguration());
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision);

        return new RevisionViewDataContainer(single, config);
    }

    // TODO: Add information of if new revision was created or not so it can be indexed as necessary
    public RevisionViewDataContainer editStudy(Long id) {
        try {
            RevisionData data = repository.editStudy(id);
            Configuration config = configService.findByTypeAndVersion(data.getConfiguration());
            TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);
            return new RevisionViewDataContainer(single, config);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
    }

    public boolean saveStudy(TransferObject to) {
        try {
            boolean result = repository.saveStudy(to);
            // Check for FileLinkQueue events.
            // If given id/revision belongs to a draft revision (and it should when we are saving) then process possible FileLinkQueue events.
            // This makes sure that any recently added file references are found from their respective REFERENCECONTAINERs.
            // Also if there is a new POR file present then that is parsed and the data is added
            if(result) {
                try {
                    repository.checkFileLinkQueue(to.getId(), to.getRevision());
                } catch(IOException ex) {
                    // TODO: better exception handling with messages to the user
                    ex.printStackTrace();
                }
            }
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveStudy(TransferObject to) {
        try {
            boolean result = repository.approveStudy(to.getId());
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }
}
