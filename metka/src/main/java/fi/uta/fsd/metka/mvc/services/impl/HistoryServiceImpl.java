package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.mvc.services.HistoryService;
import fi.uta.fsd.metka.storage.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryRepository repository;

    /*@Override public List<RevisionSO> getRevisionHistory(Long id) {
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
    }*/

    /**
     * Compares revision between two revision numbers (inclusive) and collects all changes that have happened after
     * the selected begin revision.
     * @param request User selected values for revision comparison.
     * @return
     */
    /*@Override public ChangeCompareSO compareRevisions(ChangeCompareRequest request) {

        return null;
    }*/
}
