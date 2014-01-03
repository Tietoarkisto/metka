package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import fi.uta.fsd.metka.mvc.search.SeriesSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/3/14
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SeriesFacade {

    @Autowired
    private SeriesSearch seriesSearch;

    public List<String> findAbbreviations() {
        return seriesSearch.findAbbreviations();
    }

    public List<SeriesSearchSO> searchForSeries(SeriesSearchSO query) {
        return seriesSearch.findSeries(query);
    }

    public SeriesSingleSO findSeries(SeriesSearchSO query) {
        // TODO: Actually find the series from database
        SeriesSingleSO series = new SeriesSingleSO();
        series.setId(query.getId());
        series.setAbbrevation("S1");
        series.setDescription("Testisarja id:llä "+query.getId());
        series.setName("Testisarja");

        return series;
    }
}
