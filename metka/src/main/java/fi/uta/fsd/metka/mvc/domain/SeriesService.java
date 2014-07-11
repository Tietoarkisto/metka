package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.UIRevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.SearchResult;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
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

import static fi.uta.fsd.metka.data.util.ModelValueUtil.*;

@Service
public class SeriesService {

    @Autowired
    private SeriesSearch search;
    @Autowired
    private GeneralSearch generalSearch;

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

    public List<SearchResult> searchForSeries(SeriesSearchSO query) {
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
     * Find requested revision data and its related configuration.
     * @param id RevisionableId of the requested revision.
     * @param revision Revision number of the requested revision.
     * @return RevisionViewDataContainer containing requested revision data and its configuration
     */
    public RevisionViewDataContainer findSingleRevision(Long id, Integer revision) {
        RevisionData data;
        try {
            data = generalSearch.findSingleRevision(id, revision, ConfigurationType.SERIES);
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

    public RevisionViewDataContainer newSeries() {
        RevisionData revision = null;
        try {
            revision = repository.getNew();
        } catch(Exception ex) {
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
    public RevisionViewDataContainer editSeries(Long seriesno) {
        try {
            RevisionData data = repository.editSeries(seriesno);
            Configuration config = configService.findByTypeAndVersion(data.getConfiguration());
            TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data);
            return new RevisionViewDataContainer(single, config);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
    }

    // TODO: Add information of whether changes were found or not so that unnecessary indexing can be avoided
    public boolean saveSeries(TransferObject to) {
        try {
            boolean result = repository.saveSeries(to);
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveSeries(TransferObject to) {
        try {
            boolean result = repository.approveSeries(to.getId());
            if(result)indexer.addCommand(RevisionIndexerCommand.index(indexerPaths.get("fi"), to.getId(), to.getRevision()));
            return result;
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    // Helper functions
    private SearchResult resultSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SearchResult so = new SearchResult();
        so.setId(data.getKey().getId());
        so.setRevision(data.getKey().getRevision());
        so.setState(UIRevisionState.fromRevisionState(data.getState()));
        so.setByKey("seriesno", extractIntegerSimpleValue(data.dataField(SavedDataFieldCall.get("seriesno")).getRight()));
        so.setByKey("seriesabb", extractStringSimpleValue(data.dataField(SavedDataFieldCall.get("seriesabb")).getRight()));
        so.setByKey("seriesname", extractStringSimpleValue(data.dataField(SavedDataFieldCall.get("seriesname")).getRight()));

        return so;
    }
}
