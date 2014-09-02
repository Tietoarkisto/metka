package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionHandlerRepository {

    ReturnResult claim(RevisionKey key);
    ReturnResult release(RevisionKey key);
}
