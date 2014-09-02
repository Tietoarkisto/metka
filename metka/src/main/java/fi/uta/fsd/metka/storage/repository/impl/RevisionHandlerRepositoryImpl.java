package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.RevisionHandlerRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevisionHandlerRepositoryImpl implements RevisionHandlerRepository {

    @Autowired
    private RevisionRepository revisions;

    @Override
    public ReturnResult claim(RevisionKey key) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return pair.getLeft();
        }

        RevisionData data = pair.getRight();
        if(data.getHandler() == null || !data.getHandler().equals(AuthenticationUtil.getUserName())) {
            data.setHandler(AuthenticationUtil.getUserName());
            ReturnResult result = revisions.updateRevisionData(data);
            if(result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return result;
            }
        }
        return ReturnResult.REVISION_UPDATE_SUCCESSFUL;
    }

    @Override
    public ReturnResult release(RevisionKey key) {
        Pair<ReturnResult, RevisionData> pair = revisions.getRevisionData(key);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return pair.getLeft();
        }

        RevisionData data = pair.getRight();
        if(data.getHandler() != null) {
            data.setHandler(null);
            ReturnResult result = revisions.updateRevisionData(data);
            if(result != ReturnResult.REVISION_UPDATE_SUCCESSFUL) {
                return result;
            }
        }
        return ReturnResult.REVISION_UPDATE_SUCCESSFUL;
    }
}
