package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.SearchResult;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import fi.uta.fsd.metka.mvc.domain.simple.study.StudySearchSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {
    @Autowired
    private StudyRepository repository;
    @Autowired
    private GeneralSearch generalSearch;

    @Autowired
    private ConfigurationService configService;

    public List<SearchResult> searchForStudies(StudySearchSO query) {
        List<SearchResult> resultList = new ArrayList<>();
        // TODO: Find searched studies and create search result objects for them.
        return resultList;
    }

    /**
     * Return a default revision number for requested revisionable
     * @param id Revisionable id
     * @return
     */
    public Integer findSingleRevisionNo(Integer id) {
        Integer revision = generalSearch.findSingleRevisionNo(id);
        return revision;
    }

    /**
     * Find requested revision data and convert it to SeriesSingle simple object
     * @param id Revisionable id
     * @param revision Revision number
     * @return Revision data converted to TransferObject
     */
    public RevisionViewDataContainer findSingleRevision(Integer id, Integer revision) {
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

    public RevisionViewDataContainer newStudy(Integer acquisition_number) {
        RevisionData data = null;
        try {
            data = repository.getNew(acquisition_number);
        } catch(IOException ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
        Configuration config = configService.findByTypeAndVersion(data.getConfiguration());
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);

        return new RevisionViewDataContainer(single, config);
    }

    public RevisionViewDataContainer editStudy(Integer id) {
        // TODO: Request an editable draft for given study, convert and return StudySingleSO object.
        return null;
    }

    public boolean saveStudy(TransferObject to) {
        try {
            return repository.saveStudy(to);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveStudy(TransferObject single) {
        try {
            return repository.approveStudy(single.getId());
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }
}
