package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.requests.ChangeCompareRequest;
import fi.uta.fsd.metka.mvc.services.simple.history.ChangeCompareSO;
import fi.uta.fsd.metka.mvc.services.simple.history.RevisionSO;
import fi.uta.fsd.metka.storage.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private HistoryRepository repository;

    public List<RevisionSO> getRevisionHistory(Long id) {
        List<RevisionSO> revisions = new ArrayList<RevisionSO>();
        List<RevisionData> datas = null;
        datas = repository.getRevisionHistory(id);

        for(RevisionData data : datas) {
            RevisionSO revision = new RevisionSO();
            revision.setId(data.getKey().getId());
            revision.setRevision(data.getKey().getNo());
            revision.setState(data.getState());
            if(data.isApprovedFor(Language.DEFAULT)) {
                revision.setApprovalDate(data.getApproved().get(Language.DEFAULT).getTime());
            }
            revisions.add(revision);
        }

        return revisions;
    }

    /**
     * Compares revision between two revision numbers (inclusive) and collects all changes that have happened after
     * the selected begin revision.
     * @param request User selected values for revision comparison.
     * @return
     */
    public ChangeCompareSO compareRevisions(ChangeCompareRequest request) {

        return null;
    }
}
