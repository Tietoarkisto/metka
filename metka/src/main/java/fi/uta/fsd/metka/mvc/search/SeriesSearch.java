package fi.uta.fsd.metka.mvc.search;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(readOnly = true)
public interface SeriesSearch {
    public List<String> findAbbreviations() throws IOException;

    /**
     * Return all series matching the given search terms.
     * @param query User defined search terms for finding series
     * @return List of Object[2]. First column contains the RevisionData object second column contains the removed value of the series.
     * @throws IOException
     */
    public List findSeries(SeriesSearchSO query) throws IOException;

    /**
     * Return relevant revision number for requested series.
     * If the found series has an approved revision then the latest approved revision number is returned, otherwise return
     * the draft revision number (if there is no approved or draft revision then something is horribly wrong in the database).
     *
     * @param id Id of the requested series.
     * @return Revision number of either a draft or the latest approved revision.
     * @throws IOException
     */
    public Integer findSingleSeriesRevisionNo(Integer id);

    /**
     * Return a specific revision of requested series.
     * @param id Id of the requested series
     * @param revision Revision number of requested revision.
     * @return RevisionData of the requested series revision and null if revision was not found.
     * @throws IOException
     */
    public RevisionData findSingleRevision(Integer id, Integer revision) throws IOException;
}
