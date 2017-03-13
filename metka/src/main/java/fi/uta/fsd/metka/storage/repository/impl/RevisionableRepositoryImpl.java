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

import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.RevisionableRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RevisionableRepositoryImpl implements RevisionableRepository {

    @Autowired
    private JSONUtil json;

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @CacheEvict(value="info-cache", key="#id")
    public void removeRevisionable(Long id) {
        em.remove(em.find(RevisionableEntity.class, id));
    }

    @CacheEvict(value="info-cache", key="#id")
    public void updateRevisionableRevisionNumber(Long id) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);
        entity.setLatestRevisionNo(entity.getCurApprovedNo());
    }

    @CacheEvict(value="info-cache", key="#id")
    public boolean logicallyRemoveRevisionable(DateTimeUserPair info, Long id) {
        RevisionableEntity entity = em.find(RevisionableEntity.class, id);

        //this needs to be checked so that restore-cascade can work appropriately.
        if (entity.getRemoved()) {
            return true;
        }

        entity.setRemoved(true);
        entity.setRemovalDate(info.getTime());
        entity.setRemovedBy(info.getUser());
        return false;
    }

    public long[] getAllRevisionableIds(){
        List<RevisionableEntity> list = em.createQuery("SELECT r from RevisionableEntity r").getResultList();
        long[] ids = new long[list.size()];
        for (int i = 0; i < list.size(); i++){
            ids[i] = list.get(i).getId();
        }
        return ids;
    }
}
