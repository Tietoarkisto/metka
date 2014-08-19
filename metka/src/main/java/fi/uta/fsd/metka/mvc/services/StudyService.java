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

    /*

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
