package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SeriesRepository {
    public RevisionData getNew();
    public boolean saveSeries(TransferObject so);
    public boolean approveSeries(Object seriesno);

    public RevisionData editSeries(Object seriesno);
}
