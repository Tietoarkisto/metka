package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionHandlerRepositoryImpl implements RevisionHandlerRepository {

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, TransferData> changeHandler(RevisionKey key, boolean clear) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }

        RevisionData data = pair.getRight();
        boolean update = false;
        if(clear) {
            if(data.getHandler() != null) {
                data.setHandler(null);
                update = true;
            }
        } else {
            if(!AuthenticationUtil.getUserName().equals(data.getHandler())) {
                data.setHandler(AuthenticationUtil.getUserName());
                update = true;
            }
        }

        if(update) {
            ReturnResult result = revisions.updateRevisionData(data);
            if (result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return new ImmutablePair<>(result, null);
            }
        }

        Pair<ReturnResult, RevisionableInfo> info = revisions.getRevisionableInfo(data.getKey().getId());
        if(info.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(info.getLeft(), null);
        }

        return new ImmutablePair<>(ReturnResult.REVISION_UPDATE_SUCCESSFUL, TransferData.buildFromRevisionData(data, info.getRight()));
    }
}
