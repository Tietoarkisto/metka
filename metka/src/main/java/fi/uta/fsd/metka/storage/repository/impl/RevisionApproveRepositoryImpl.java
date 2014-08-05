package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.RevisionApproveRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionApproveRepositoryImpl implements RevisionApproveRepository {
    @Override
    public Pair<ReturnResult, TransferData> approve(TransferData transferData) {
        // TODO: General and type specific approvals
        return null;
    }
}
