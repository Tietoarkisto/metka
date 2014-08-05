package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.SeriesRepository;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeriesService {

    @Autowired
    private SeriesSearch search;
    @Autowired
    private GeneralRepository general;

    @Autowired
    private SeriesRepository repository;
    @Autowired
    private ConfigurationService configService;

    @Autowired
    private IndexerComponent indexer; // Provide revision indexing services.

    private static Map<String, DirectoryManager.DirectoryPath> indexerPaths = new HashMap<>();

    static {
        indexerPaths.put("fi", DirectoryManager.formPath(false, IndexerConfigurationType.REVISION, "fi", ConfigurationType.SERIES.toValue()));
    }

    public List<String> findAbbreviations() {
        List<String> list = null;
        try {
            list = search.findAbbreviations();
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            list = new ArrayList<String>();
            list.add("");
        }
        return list;
    }

    /*public List<SearchResult> searchForSeries(SeriesSearchSO query) {
        List<SearchResult> seriesList = new ArrayList<>();
        List<RevisionDataRemovedContainer> datas = null;
        try {
            datas = search.findSeries(query);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return seriesList;
        }

        for(RevisionDataRemovedContainer container : datas) {
            SearchResult series = resultSOFromRevisionData(container.getData());
            if(series != null) {
                if(container.isRemoved()) {
                    series.setState(UIRevisionState.REMOVED);
                }
                seriesList.add(series);
            }
        }
        return seriesList;
    }*/

    /**
     * Return a default revision number for requested revisionable
     * @param id Revisionable id
     * @return
     */
    /*public Pair<ReturnResult, Integer> findSingleRevisionNo(Long id) {
        return general.getLatestRevisionNoForIdAndType(id, false, ConfigurationType.SERIES);
    }*/

    /**
     * Find requested revision data and its related configuration.
     * @param id RevisionableId of the requested revision.
     * @param revision Revision number of the requested revision.
     * @return RevisionViewDataContainer containing requested revision data and its configuration
     */
    /*public RevisionViewDataContainer findSingleRevision(Long id, Integer revision) {

        Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(id, revision, ConfigurationType.SERIES);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: return actual error value to user
            return null;
        }
        RevisionData data = pair.getRight();
        Configuration config = configService.findByTypeAndVersion(data.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);

        return new RevisionViewDataContainer(single, config);
    }*/

    /*public RevisionViewDataContainer newSeries() {
        RevisionData revision = null;
        try {
            revision = repository.getNew();
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        // Creating new series was successful, index series
        indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), revision.getKey().getId(), revision.getKey().getNo()));

        Configuration config = configService.findByTypeAndVersion(revision.getConfiguration()).getRight();
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision);

        return new RevisionViewDataContainer(single, config);
    }*/

    // TODO: Add information of if new revision was created or not so it can be indexed as necessary
    /*public RevisionViewDataContainer editSeries(Long seriesno) {
        try {
            RevisionData data = repository.editSeries(seriesno);
            Configuration config = configService.findByTypeAndVersion(data.getConfiguration()).getRight();
            TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);
            return new RevisionViewDataContainer(single, config);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
    }*/

    // TODO: Add information of whether changes were found or not so that unnecessary indexing can be avoided
    /*public boolean saveSeries(TransferObject to) {
        try {
            boolean result = repository.saveSeries(to);
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }*/

    /*public boolean approveSeries(TransferObject to) {
        try {
            boolean result = repository.approveSeries(to.getId());
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }*/

    // Helper functions
    /*private SearchResult resultSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SearchResult so = new SearchResult();
        so.setId(data.getKey().getId());
        so.setRevision(data.getKey().getNo());
        so.setState(UIRevisionState.fromRevisionState(data.getState()));
        so.setByKey("seriesid", SavedDataField.valueAsInteger(data.dataField(SavedDataFieldCall.get("seriesid")).getRight()));
        so.setByKey("seriesabbr", SavedDataField.valueAsString(data.dataField(SavedDataFieldCall.get("seriesabbr")).getRight()));
        so.setByKey("seriesname", SavedDataField.valueAsString(data.dataField(SavedDataFieldCall.get("seriesname")).getRight()));

        return so;
    }*/
}
