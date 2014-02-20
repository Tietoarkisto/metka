package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.enums.repositoryResponses.RemoveResponse;
import javassist.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.MissingResourceException;

/**
 * Contains methods for general operations that don't require their own repositories.
 */
@Transactional(readOnly = true, noRollbackFor = {NotFoundException.class, MissingResourceException.class})
public interface GeneralRepository {

    public Integer getAdjancedRevisionableId(Integer currentId, String type, boolean forward) throws NotFoundException;
    @Transactional(readOnly = false) public RemoveResponse removeDraft(String type, Integer id);
    @Transactional(readOnly = false) public RemoveResponse removeLogical(String type, Integer id);
}
