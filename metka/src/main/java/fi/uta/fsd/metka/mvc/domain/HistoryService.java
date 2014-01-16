package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.repository.HistoryRepository;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.history.RevisionSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/14/14
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class HistoryService {

    @Autowired
    private HistoryRepository repository;

    public List<RevisionSO> getRevisionHistory(Integer id) {
        List<RevisionSO> revisions = new ArrayList<RevisionSO>();
        List<RevisionData> datas = null;
        try {
            datas = repository.getRevisionHistory(id);
        } catch(IOException ex) {
            // TODO: Notify user of problems with fetching revision history
            return revisions;
        }

        for(RevisionData data : datas) {
            RevisionSO revision = new RevisionSO();
            revision.setId(data.getKey().getId());
            revision.setRevision(data.getKey().getRevision());
            revision.setState(data.getState());
            revision.setApprovalDate(data.getApprovalDate());
            revisions.add(revision);
        }

        return revisions;
    }
}
