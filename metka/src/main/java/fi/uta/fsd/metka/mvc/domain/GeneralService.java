package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.repository.GeneralRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/24/14
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GeneralService {

    @Autowired
    private GeneralRepository repository;

    /**
     * Return the id of next or previous revisionable of the same type as the current revisionable the user is looking at.
     * Used to navigate to previous or next object.
     *
     * @param currentId Id of the revisionable the user is looking at at the moment
     * @param type What type of revisionable is required (series, publication etc.)
     * @param forward do we want next or previous revisionable
     * @return
     */
    public Integer getAdjancedRevisionableId(Integer currentId, String type, boolean forward) throws NotFoundException {
        return repository.getAdjancedRevisionableId(currentId, type, forward);
    }
}
