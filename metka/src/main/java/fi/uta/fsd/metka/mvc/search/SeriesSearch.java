package fi.uta.fsd.metka.mvc.search;

import fi.uta.fsd.metka.mvc.services.simple.series.SeriesSearchSO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface SeriesSearch {
    public List<String> findAbbreviations();

    /**
     * Return all series matching the given search terms.
     * @param query User defined search terms for finding series
     * @return List of Object[2]. First column contains the RevisionData object second column contains the removed value of the series.
     */
    public List<RevisionDataRemovedContainer> findSeries(SeriesSearchSO query);
}
