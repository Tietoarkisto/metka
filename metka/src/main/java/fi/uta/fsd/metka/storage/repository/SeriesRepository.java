package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface SeriesRepository {
    public RevisionData getNew() throws IOException;
    public boolean saveSeries(TransferObject so) throws IOException;
    public boolean approveSeries(Object seriesno) throws IOException;

    public RevisionData editSeries(Object seriesno) throws IOException;
}
