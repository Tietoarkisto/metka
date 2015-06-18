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
