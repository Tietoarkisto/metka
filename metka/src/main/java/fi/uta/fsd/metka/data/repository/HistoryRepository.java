package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.requests.ChangeCompareRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Transactional(readOnly = true)
public interface HistoryRepository {
    public List<RevisionData> getRevisionHistory(Integer id) throws IOException;

    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request) throws IOException;
    public RevisionData getRevisionByKey(Integer id, Integer revision) throws IOException;
}
