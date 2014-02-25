package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface SeriesRepository {
    public RevisionData getNew() throws IOException;
    public boolean saveSeries(TransferObject so) throws IOException;
    public boolean approveSeries(Object seriesno) throws IOException;

    public RevisionData editSeries(Object seriesno) throws IOException;
}
