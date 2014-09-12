package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionHandlerRepository {

    /**
     * Changes revision handler if revision can be found and there is a need to change the handler.
     * @param key      RevisionKey to find the revision
     * @param clear    Should handler be cleared or set to current user
     * @return  Pair
     */
    Pair<ReturnResult, TransferData> changeHandler(RevisionKey key, boolean clear);
}
