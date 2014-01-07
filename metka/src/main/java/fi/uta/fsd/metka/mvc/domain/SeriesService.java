package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.SeriesRepository;
import fi.uta.fsd.metka.model.data.Change;
import fi.uta.fsd.metka.model.data.FieldContainer;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.SimpleValue;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

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
        return search.findAbbreviations();
    }

    public List<SeriesSearchSO> searchForSeries(SeriesSearchSO query) {
        List<RevisionData> datas = search.findSeries(query);
        List<SeriesSearchSO> seriesList = new ArrayList<SeriesSearchSO>();
        for(RevisionData data : datas) {
            SeriesSearchSO series = searchSOFromRevisionData(data);
            if(series != null) {
                seriesList.add(series);
            }
        }
        return seriesList;
    }

    public SeriesSingleSO findSingleSeries(SeriesSearchSO query) {
        // TODO: Actually find the series from database
        SeriesSingleSO series = new SeriesSingleSO();
        series.setId(query.getId());
        series.setAbbreviation("S1");
        series.setDescription("Testisarja id:llä "+query.getId());
        series.setName("Testisarja");

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

    // Helper functions
    private SeriesSingleSO singleSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SeriesSingleSO so = new SeriesSingleSO();
        // TODO: this should be automated as much as possible using configuration in the future.
        so.setId(extractIntegerValue(getContainerFromRevisionData(data, "id")));
        so.setAbbreviation(extractStringValue(getContainerFromRevisionData(data, "abbreviation")));
        so.setName(extractStringValue(getContainerFromRevisionData(data, "name")));
        so.setDescription(extractStringValue(getContainerFromRevisionData(data, "description")));

        return so;
    }

    private SeriesSearchSO searchSOFromRevisionData(RevisionData data) {
        // check if data is for series
        if(data == null || data.getConfiguration().getType() != ConfigurationType.SERIES) {
            return null;
        }

        SeriesSearchSO so = new SeriesSearchSO();
        // TODO: this should be automated as much as possible using configuration in the future.
        so.setId(extractIntegerValue(getContainerFromRevisionData(data, "id")));
        so.setAbbreviation(extractStringValue(getContainerFromRevisionData(data, "abbreviation")));
        so.setName(extractStringValue(getContainerFromRevisionData(data, "name")));

        return so;
    }
}
