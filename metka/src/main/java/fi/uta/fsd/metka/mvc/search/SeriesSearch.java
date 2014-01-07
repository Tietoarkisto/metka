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
    public List<String> findAbbreviations() throws JsonParseException, JsonMappingException, IOException;
    public List<RevisionData> findSeries(SeriesSearchSO query);
}
