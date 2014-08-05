package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionCreateRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionCreationRepositoryImpl implements RevisionCreationRepository {
    @Override
    public Pair<ReturnResult, TransferData> create(RevisionCreateRequest request) {
        // TODO: Implement type specific creation requests
        return null;
    }
}
