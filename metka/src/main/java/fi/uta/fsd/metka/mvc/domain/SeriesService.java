package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.UIRevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.SearchResult;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

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
    public Integer findSingleRevisionNo(Integer id) {
        Integer revision = generalSearch.findSingleRevisionNo(id);
        return revision;
    }

    /**
     * Find requested revision data and its related configuration.
     * @param id RevisionableId of the requested revision.
     * @param revision Revision number of the requested revision.
     * @return RevisionViewDataContainer containing requested revision data and its configuration
     */
    public RevisionViewDataContainer findSingleRevision(Integer id, Integer revision) {
        RevisionData data = null;
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
        Configuration config = configService.findByTypeAndVersion(revision.getConfiguration());
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision);

        return new RevisionViewDataContainer(single, config);
    }

    public RevisionViewDataContainer editSeries(Integer seriesno) {
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

    public boolean saveSeries(TransferObject to) {
        try {
            return repository.saveSeries(to);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveSeries(TransferObject to) {
        try {
            return repository.approveSeries(to.getId());
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
        so.setByKey("seriesno", extractIntegerSimpleValue(getSavedFieldContainerFromRevisionData(data, "seriesno")));
        so.setByKey("seriesabb", extractStringSimpleValue(getSavedFieldContainerFromRevisionData(data, "seriesabb")));
        so.setByKey("seriesname", extractStringSimpleValue(getSavedFieldContainerFromRevisionData(data, "seriesname")));

        return so;
    }
}
