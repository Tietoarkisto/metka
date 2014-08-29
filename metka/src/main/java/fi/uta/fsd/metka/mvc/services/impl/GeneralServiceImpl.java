package fi.uta.fsd.metka.mvc.services.impl;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralServiceImpl implements GeneralService {

    @Autowired
    private RevisionRepository revisions;

    // TODO: Move to revision service
    /**
     * Return the id of next or previous revisionable of the same type as the current revisionable the user is looking at.
     * Used to navigate to previous or next object.
     *
     * @param currentId Id of the revisionable the user is looking at at the moment
     * @param type What type of revisionable is required (series, publication etc.)
     * @param forward do we want next or previous revisionable
     * @return Id of the adjanced revisionable object. If not found then error is thrown instead.
     */
    @Override public Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, String type, boolean forward) {
        return revisions.getAdjacentRevisionableId(currentId, type, forward);
    }

    /**
     * Returns a RevisionData for a specific revision id and number.
     * Doesn't check the returned revision for type.
     * @param id
     * @param revision
     * @return
     */
    @Override public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision) {
        return revisions.getRevisionData(id, revision);
    }
}
