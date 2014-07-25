package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.requests.ChangeCompareRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Transactional(readOnly = true)
public interface HistoryRepository {
    public List<RevisionData> getRevisionHistory(Long id) throws IOException;

    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request) throws IOException;
    public RevisionData getRevisionByKey(Long id, Integer revision) throws IOException;
}
