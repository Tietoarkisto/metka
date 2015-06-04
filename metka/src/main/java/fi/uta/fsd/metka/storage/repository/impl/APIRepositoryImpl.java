package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.storage.entity.APIUserEntity;
import fi.uta.fsd.metka.storage.repository.APIRepository;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * This provides only methods for accessing API User information.
 * The actual creation of API users is done in APIUserRepository access to which is restricted to MetkaUI
 */
@Repository
public class APIRepositoryImpl implements APIRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public APIUser getAPIUser(String key) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.publicKey=:key", APIUserEntity.class)
                .setParameter("key", key)
                .getResultList();

        if(entities.isEmpty()) {
            return null;
        }

        if(entities.size() > 1) {
            Logger.error(getClass(), "Found " + entities.size() + " API users with public key " + key);
            return null;
        }

        APIUserEntity entity = entities.get(0);
        APIUser user = new APIUser();
        user.setName(entity.getName());
        user.setSecret(entity.getSecret());
        user.setPermissions(entity.getPermissions());
        user.setLastAccess(entity.getLastAccess());
        return user;
    }

    @Override
    public void updateAPIAccess(String key) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.publicKey=:key", APIUserEntity.class)
                .setParameter("key", key)
                .getResultList();

        if(entities.isEmpty()) {
            return;
        }

        if(entities.size() > 1) {
            Logger.error(getClass(), "Found "+entities.size()+" API users with public key "+key);
        }

        for(APIUserEntity entity : entities) {
            entity.setLastAccess(new LocalDateTime());
        }
    }
}
