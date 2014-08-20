package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {

    @Autowired
    private GeneralRepository repository;

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
    public Pair<ReturnResult, Long> getAdjancedRevisionableId(Long currentId, String type, boolean forward) {
        return repository.getAdjacentRevisionableId(currentId, type, forward);
    }

    /**
     * Returns a RevisionData for a specific revision id and number.
     * Doesn't check the returned revision for type.
     * @param id
     * @param revision
     * @return
     */
    public Pair<ReturnResult, RevisionData> getRevisionData(Long id, Integer revision) {
        return repository.getRevisionData(id, revision);
    }
}
