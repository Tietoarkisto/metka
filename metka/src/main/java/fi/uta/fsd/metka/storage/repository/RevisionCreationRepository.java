package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RevisionCreationRepository {
    public Pair<ReturnResult, TransferData> create(RevisionCreateRequest request);
}
