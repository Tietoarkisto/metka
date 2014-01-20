package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.requests.ChangeCompareRequest;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/14/14
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HistoryRepository {
    public List<RevisionData> getRevisionHistory(Integer id) throws IOException;

    public List<RevisionData> getRevisionsForComparison(ChangeCompareRequest request) throws IOException;
    public RevisionData getRevisionByKey(Integer id, Integer revision) throws IOException;
}
