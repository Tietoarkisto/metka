package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionRestoreRepository {
    public RemoveResult restore(RevisionKey key);
}
