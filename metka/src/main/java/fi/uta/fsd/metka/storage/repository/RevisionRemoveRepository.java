package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.RemoveResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionRemoveRepository {
    public RemoveResult remove(TransferData transferData, DateTimeUserPair info);
    public RemoveResult removeDraft(TransferData transferData, DateTimeUserPair info);
    public RemoveResult removeLogical(TransferData transferData, DateTimeUserPair info);
}
