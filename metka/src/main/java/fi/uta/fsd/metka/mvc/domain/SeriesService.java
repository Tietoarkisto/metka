package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.data.*;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
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

    public List<SeriesSearchSO> searchForSeries(SeriesSearchSO query) {
        List<SeriesSearchSO> seriesList = new ArrayList<SeriesSearchSO>();
        List<RevisionData> datas = null;
        try {
            datas = search.findSeries(query);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return seriesList;
        }

        for(RevisionData data : datas) {
            SeriesSearchSO series = searchSOFromRevisionData(data);
            if(series != null) {
                seriesList.add(series);
            }
        }
        return seriesList;
    }

    public SeriesSingleSO findSingleSeries(Integer id) {
        RevisionData data = null;
        try {
            data = search.findSingleSeries(id);
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

    public SeriesSingleSO findDraftRevision(SeriesSearchSO query) {
        return null;  //To change body of created methods use File | Settings | File Templates.
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

    public boolean approveSeries(Integer id) {
        try {
            return repository.approveSeries(id);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return false;
        }
    }

    public SeriesSingleSO editSeries(Integer id) {
        try {
            RevisionData data = repository.editSeries(id);
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
        so.setId(extractIntegerSimpleValue(getContainerFromRevisionData(data, "id")));
        so.setRevision(data.getKey().getRevision());
        so.setAbbreviation(extractStringSimpleValue(getContainerFromRevisionData(data, "abbreviation")));
        so.setName(extractStringSimpleValue(getContainerFromRevisionData(data, "name")));
        so.setDescription(extractStringSimpleValue(getContainerFromRevisionData(data, "description")));
        so.setState(data.getState());
        return so;
    }

    private SeriesSearchSO searchSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SeriesSearchSO so = new SeriesSearchSO();
        // TODO: this should be automated as much as possible using configuration in the future.
        so.setId(extractIntegerSimpleValue(getContainerFromRevisionData(data, "id")));
        so.setAbbreviation(extractStringSimpleValue(getContainerFromRevisionData(data, "abbreviation")));
        so.setName(extractStringSimpleValue(getContainerFromRevisionData(data, "name")));

        return so;
    }
}
