package fi.uta.fsd.metka.data.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface SeriesRepository {
    public RevisionData getNew() throws IOException;
    public boolean saveSeries(SeriesSingleSO so) throws IOException;
    public boolean approveSeries(Object seriesno) throws IOException;

    public RevisionData editSeries(Object seriesno) throws IOException;
}
