package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SeriesRepository {
    public RevisionData getNew();
    public boolean approveSeries(Object seriesno);

    public RevisionData editSeries(Object seriesno);
}
