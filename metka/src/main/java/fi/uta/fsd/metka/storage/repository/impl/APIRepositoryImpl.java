/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.storage.entity.APIUserEntity;
import fi.uta.fsd.metka.storage.repository.APIRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.APIUserEntry;
import fi.uta.fsd.metka.transfer.settings.NewAPIUserRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.codec.digest.Sha2Crypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
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
    public APIUserEntry getAPIUser(String userName) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.userName=:userName", APIUserEntity.class)
                .setParameter("userName", userName)
                .getResultList();

        if(entities.isEmpty()) {
            return null;
        }

        if(entities.size() > 1) {
            Logger.error(getClass(), "Found " + entities.size() + " API users with user name " + userName);
            return null;
        }

        APIUserEntity entity = entities.get(0);
        return formAPIUserEntry(entity);
    }

    @Override
    public void updateAPIAccess(String userName) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.userName=:userName", APIUserEntity.class)
                .setParameter("userName", userName)
                .getResultList();

        if(entities.isEmpty()) {
            return;
        }

        if(entities.size() > 1) {
            Logger.error(getClass(), "Found "+entities.size()+" API users with user name "+userName);
        }

        for(APIUserEntity entity : entities) {
            entity.setLastAccess(new LocalDateTime());
        }
    }

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
    public ReturnResult removeAPIUser(String userName) {
        List<APIUserEntity> entities = em.createQuery("SELECT u FROM APIUserEntity u WHERE u.userName=:userName", APIUserEntity.class)
                .setParameter("userName", userName)
                .getResultList();

        if(entities.isEmpty()) {
            return ReturnResult.OPERATION_FAIL;
        }

        if(entities.size() > 1) {
            Logger.error(getClass(), "Found " + entities.size() + " API users with user name " + userName);
            return ReturnResult.OPERATION_FAIL;
        }

        em.remove(entities.get(0));

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public Pair<ReturnResult, APIUserEntry> newAPIUser(NewAPIUserRequest request) {
        if(StringUtils.isBlank(request.getUsername()) || StringUtils.isBlank(request.getRole())) {
            return new ImmutablePair<>(ReturnResult.PARAMETERS_MISSING, null);
        }
        APIUserEntity entity = new APIUserEntity();
        entity.setLastAccess(new LocalDateTime());

        entity.setUserName(request.getUsername());
        entity.setName(request.getName());
        entity.setCreatedBy(AuthenticationUtil.getUserName());
        entity.setRole(request.getRole());
        // Let's create the secret for signatures. This is going to be a bit more complex than public key
        String secret = (new LocalDateTime()).toString()+entity.getCreatedBy()+entity.getUserName()+entity.getRole();
        secret = new String(Base64.encode(Sha2Crypt.sha256Crypt(secret.getBytes()).getBytes()));
        entity.setSecret(secret);

        em.persist(entity);
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, formAPIUserEntry(entity));
    }

    private APIUserEntry formAPIUserEntry(APIUserEntity entity) {
        APIUserEntry user = new APIUserEntry();
        user.setUserName(entity.getUserName());
        user.setName(entity.getName());
        user.setSecret(entity.getSecret());
        user.setLastAccess(entity.getLastAccess());
        user.setCreateBy(entity.getCreatedBy());
        user.setRole(entity.getRole());
        return user;
    }
}
