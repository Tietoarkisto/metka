package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.APIUserEntity;
import fi.uta.fsd.metka.storage.repository.APIUserRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.APIUserEntry;
import fi.uta.fsd.metka.transfer.settings.NewAPIUserRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaExternal.ExternalUtil;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionality for listing, creating and removing API Users
 */
@Repository
public class APIUserRepositoryImpl implements APIUserRepository {
    private static Logger logger = LoggerFactory.getLogger(APIUserRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public Pair<ReturnResult, List<APIUserEntry>> listAPIUsers() {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u ORDER BY u.name", APIUserEntity.class).getResultList();
        List<APIUserEntry> users = new ArrayList<>();
        if(entities.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.NO_RESULTS,users);
        }
        for(APIUserEntity entity : entities) {
            users.add(formAPIUserEntry(entity));
        }
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, users);
    }

    @Override
    public ReturnResult removeAPIUser(String key) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.publicKey=:key", APIUserEntity.class)
                .setParameter("key", key)
                .getResultList();

        if(entities.isEmpty()) {
            return ReturnResult.OPERATION_FAIL;
        }

        if(entities.size() > 1) {
            logger.error("Found "+entities.size()+" API users with public key "+key);
            return ReturnResult.OPERATION_FAIL;
        }

        em.remove(entities.get(0));

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public Pair<ReturnResult, APIUserEntry> newAPIUser(NewAPIUserRequest request) {
        APIUserEntity entity = new APIUserEntity();
        entity.setLastAccess(new LocalDateTime());

        entity.setName(request.getName());
        entity.setCreatedBy(AuthenticationUtil.getUserName());

        short permissions = 0b00000000;
        if(request.isHasStudyCreatePermission()) permissions = (short)(permissions | ExternalUtil.FLAG_STUDY_CREATE);
        if(request.isHasSearchPermission()) permissions = (short)(permissions | ExternalUtil.FLAG_SEARCH);
        if(request.isHasReadPermission()) permissions = (short)(permissions | ExternalUtil.FLAG_READ);
        if(request.isHasEditPermission()) permissions = (short)(permissions | ExternalUtil.FLAG_EDIT);
        entity.setPermissions(permissions);

        em.persist(entity);

        LocalDateTime creationTime = new LocalDateTime();
        // Let's create a public key (this won't be simple either but it's less complex than secret key)
        String publicKey = entity.getApiUserId()+entity.getCreatedBy()+creationTime.toString();
        publicKey = new String(Base64.encode(Sha2Crypt.sha256Crypt(publicKey.getBytes()).getBytes()));
        entity.setPublicKey(publicKey);

        // Let's create the secret for signatures. This is going to be a bit more complex than public key
        String secret = creationTime.toString()+publicKey+entity.getName()+entity.getApiUserId()+permissions;
        secret = new String(Base64.encode(Sha2Crypt.sha512Crypt(secret.getBytes()).getBytes()));
        entity.setSecret(secret);
        return null;
    }

    private APIUserEntry formAPIUserEntry(APIUserEntity entity) {
        APIUserEntry user = new APIUserEntry();
        user.setName(entity.getName());
        user.setPublicKey(entity.getPublicKey());
        user.setSecret(entity.getSecret());
        user.setLastAccess(entity.getLastAccess());
        user.setCreateBy(entity.getCreatedBy());
        user.setHasStudyCreatePermission(checkPermission(ExternalUtil.FLAG_STUDY_CREATE, entity.getPermissions()));
        user.setHasSearchPermission(checkPermission(ExternalUtil.FLAG_SEARCH, entity.getPermissions()));
        user.setHasReadPermission(checkPermission(ExternalUtil.FLAG_READ, entity.getPermissions()));
        user.setHasEditPermission(checkPermission(ExternalUtil.FLAG_EDIT, entity.getPermissions()));
        return user;
    }

    private boolean checkPermission(short flag, short permissions) {
        return (flag & permissions) == flag;
    }
}
