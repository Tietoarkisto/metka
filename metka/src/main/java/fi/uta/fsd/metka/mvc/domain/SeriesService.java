package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.data.*;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchResultSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import fi.uta.fsd.metka.mvc.search.GeneralSearch;
import fi.uta.fsd.metka.mvc.search.RevisionDataRemovedContainer;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/3/14
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SeriesService {

    @Autowired
    private SeriesSearch search;
    @Autowired
    private GeneralSearch generalSearch;

    @Autowired
    private SeriesRepository repository;

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
     * Find requested revision data and convert it to SeriesSingle simple object
     * @param id Revisionable id
     * @param revision Revision number
     * @return Revision data converted to SeriesSingleSO
     */
    public SeriesSingleSO findSingleRevision(Integer id, Integer revision) {
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

        SeriesSingleSO series = singleSOFromRevisionData(data);

        return series;
    }

    public SeriesSingleSO newSeries() {
        RevisionData revision = null;
        try {
            revision = repository.getNew();
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        SeriesSingleSO single = singleSOFromRevisionData(revision);

        return single;
    }

    public boolean saveSeries(SeriesSingleSO so) {
        try {
            return repository.saveSeries(so);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public boolean approveSeries(SeriesSingleSO so) {
        try {
            return repository.approveSeries(so.getByKey("seriesno"));
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public SeriesSingleSO editSeries(Integer seriesno) {
        try {
            RevisionData data = repository.editSeries(seriesno);
            return singleSOFromRevisionData(data);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }
    }













    // Helper functions

    private SeriesSingleSO singleSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SeriesSingleSO so = new SeriesSingleSO();
        // TODO: this should be automated as much as possible using configuration in the future.
        // TODO: For now assumes that all fields are ValueFields
        so.setRevision(data.getKey().getRevision());
        so.setState(data.getState());
        so.setSeriesno(extractIntegerSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesno")));
        so.setSeriesabb(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesabb")));
        so.setSeriesname(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesname")));
        so.setSeriesdesc(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesdesc")));
        so.setSeriesnotes(extractStringSimpleValue(getValueFieldContainerFromRevisionData(data, "seriesnotes")));
        return so;
    }

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
