package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionApproveRepository {
    public Pair<ReturnResult, TransferData> approve(TransferData transferData, DateTimeUserPair info);
}
