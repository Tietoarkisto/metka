package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.RevisionEditRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionEditRepositoryImpl implements RevisionEditRepository {
    @Override
    public Pair<ReturnResult, TransferData> edit(TransferData transferData) {
        // TODO: Implement type specific edit commands
        return null;
    }
}
