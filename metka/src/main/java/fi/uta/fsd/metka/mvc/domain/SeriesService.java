package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchResultSO;
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

    public List<SeriesSearchResultSO> searchForSeries(SeriesSearchSO query) {
        List<SeriesSearchResultSO> seriesList = new ArrayList<>();
        List<RevisionDataRemovedContainer> datas = null;
        try {
            datas = search.findSeries(query);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return seriesList;
        }

        for(RevisionDataRemovedContainer container : datas) {
            SeriesSearchResultSO series = resultSOFromRevisionData(container.getData());
            if(series != null) {
                if(container.isRemoved()) {
                    series.setState("removed");
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
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data, config);

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
        TransferObject single = TransferObject.buildTransferObjectFromRevisionData(revision, config);

        return new RevisionViewDataContainer(single, config);
    }

    public RevisionViewDataContainer editSeries(Integer seriesno) {
        try {
            RevisionData data = repository.editSeries(seriesno);
            Configuration config = configService.findByTypeAndVersion(data.getConfiguration());
            TransferObject single = TransferObject.buildTransferObjectFromRevisionData(data, config);
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

    /*private TransferObject transferObjectFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        TransferObject to = new TransferObject();
        // TODO: this should be automated as much as possible using configuration in the future.
        // TODO: For now assumes that all fields are ValueFields
        to.setId(data.getKey().getId());
        to.setRevision(data.getKey().getRevision());
        to.setState(data.getState());
        to.setConfiguration(data.getConfiguration());
        to.setByKey("seriesno", extractIntegerSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesno")));
        to.setByKey("seriesabb", extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesabb")));
        to.setByKey("seriesname", extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesname")));
        to.setByKey("seriesdesc", extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesdesc")));
        to.setByKey("seriesnotes", extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesnotes")));

        return to;
    }*/

    private SeriesSearchResultSO resultSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SeriesSearchResultSO so = new SeriesSearchResultSO();
        // TODO: this should be automated as much as possible using configuration in the future.
        so.setRevision(data.getKey().getRevision());
        so.setSeriesno(extractIntegerSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesno")));
        so.setSeriesabb(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesabb")));
        so.setSeriesname(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesname")));
        switch(data.getState()) {
            case DRAFT:
                so.setState("draft");
                break;
            case APPROVED:
                so.setState("approved");
                break;
        }

        return so;
    }
}
