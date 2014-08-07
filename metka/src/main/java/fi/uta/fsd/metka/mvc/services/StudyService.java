package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.search.GeneralSearch;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyService {
    @Autowired
    private GeneralSearch generalSearch;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private ConfigurationService configService;

    @Autowired
    private IndexerComponent indexer; // Provide revision indexing services.

    // TODO: Check that these work in the generalized revision versions

    /*public List<SearchResult> searchForStudies(StudySearchSO query) {
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
    }*/

    // Helper functions
    /*private SearchResult resultSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.STUDY) {
            return null;
        }

        SearchResult so = new SearchResult();
        so.setId(data.getKey().getId());
        so.setRevision(data.getKey().getNo());
        so.setState(UIRevisionState.fromRevisionState(data.getState()));
        so.setByKey("studyid", SavedDataField.valueAsInteger(data.dataField(SavedDataFieldCall.get("studyid")).getRight()));
        so.setByKey("title", SavedDataField.valueAsString(data.dataField(SavedDataFieldCall.get("title")).getRight()));

        return so;
    }*/

    /**
     * Return a default revision number for requested revisionable
     * @param id Revisionable id
     * @return
     */
    /*public Pair<ReturnResult, Integer> findSingleRevisionNo(Long id) {
        return general.getLatestRevisionNoForIdAndType(id, false, ConfigurationType.STUDY);
    }*/

    /**
     * Find requested revision data and convert it to SeriesSingle simple object
     * @param id Revisionable id
     * @param revision Revision number
     * @return Revision data converted to TransferObject
     */
    /*public RevisionViewDataContainer findSingleRevision(Long id, Integer revision) {

        // Check for FileLinkQueue events.
        // If given id/revision belongs to a draft revision then process possible FileLinkQueue events.
        // This makes sure that any recently added file references are found from their respective REFERENCECONTAINERs.
        // Also if there is a new POR file present then that is parsed and the data is added
        repository.checkFileLinkQueue(id, revision);

        Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(id, revision, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: return actual error value to user
            return null;
        }
        RevisionData data = pair.getRight();
        Configuration config = configService.findByTypeAndVersion(data.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);

        return new RevisionViewDataContainer(single, config);
    }

    public RevisionViewDataContainer newStudy(Long acquisition_number) {
        RevisionData revision = repository.getNew(acquisition_number);
        if(revision == null) {
            return null;
        }

        // Creating new series was successful, index series
        indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), revision.getKey().getId(), revision.getKey().getNo()));

        Configuration config = configService.findByTypeAndVersion(revision.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision);

        return new RevisionViewDataContainer(single, config);
    }

    // TODO: Add information of if new revision was created or not so it can be indexed as necessary
    public RevisionViewDataContainer editStudy(Long id) {
        RevisionData data = repository.editStudy(id);
        Configuration config = configService.findByTypeAndVersion(data.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);
        return new RevisionViewDataContainer(single, config);
    }

    public boolean saveStudy(TransferObject to) {
        boolean result = repository.saveStudy(to);
        // Check for FileLinkQueue events.
        // If given id/revision belongs to a draft revision (and it should when we are saving) then process possible FileLinkQueue events.
        // This makes sure that any recently added file references are found from their respective REFERENCECONTAINERs.
        // Also if there is a new POR file present then that is parsed and the data is added
        if(result) {
            repository.checkFileLinkQueue(to.getId(), to.getRevision());
        }
        if(result) {
            try {
                indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            } catch(Exception ex) {
                // TODO: better exception handling with messages to the user
                ex.printStackTrace();
            }
        }
        return result;
    }

    public boolean approveStudy(TransferObject to) {
        boolean result = repository.approveStudy(to.getId());
        if(result) {
            try {
                indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            } catch(Exception ex) {
                // TODO: better exception handling with messages to the user
                ex.printStackTrace();
            }
        }
        return result;
    }*/
}
