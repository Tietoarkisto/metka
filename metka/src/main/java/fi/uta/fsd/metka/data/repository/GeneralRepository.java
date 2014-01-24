package fi.uta.fsd.metka.data.repository;

import javassist.NotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains methods for general operations that don't require their own repositories.
 * Doing changes through these methods should be avoided if possible.
 */
@Transactional(readOnly = true)
public interface GeneralRepository {

    public Integer getAdjancedRevisionableId(Integer currentId, String type, boolean forward) throws NotFoundException;
}
