package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.requests.ChangeCompareRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface HistoryRepository {
    public List<RevisionData> getRevisionHistory(Long id);

    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request);
}
