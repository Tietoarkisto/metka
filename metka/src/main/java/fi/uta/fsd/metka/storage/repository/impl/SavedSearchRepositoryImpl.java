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

import fi.uta.fsd.metka.storage.entity.SavedExpertSearchEntity;
import fi.uta.fsd.metka.storage.repository.SavedSearchRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SavedSearchRepositoryImpl implements SavedSearchRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public List<SavedExpertSearchItem> listSavedSearches() {
        List<SavedExpertSearchItem> items = new ArrayList<>();
        // This gets all saved searches
        List<SavedExpertSearchEntity> entities = em.createQuery("SELECT e FROM SavedExpertSearchEntity e", SavedExpertSearchEntity.class)
                .getResultList();

        // This gets only the searches current user has saved
        /*List<SavedExpertSearchEntity> entities = em.createQuery("SELECT e FROM SavedExpertSearchEntity e WHERE e.savedBy=:user", SavedExpertSearchEntity.class)
                .setParameter("user", AuthenticationUtil.getUserName())
                .getResultList();*/
        for(SavedExpertSearchEntity entity : entities) {
            SavedExpertSearchItem item = formSavedExpertSearchItem(entity);
            items.add(item);
        }
        return items;
    }

    @Override
    public SavedExpertSearchItem saveExpertSearch(SavedExpertSearchItem search) {
        SavedExpertSearchEntity entity = new SavedExpertSearchEntity();
        entity.setTitle(search.getTitle());
        entity.setQuery(search.getQuery());
        entity.setSavedAt(new LocalDate());
        entity.setSavedBy(AuthenticationUtil.getUserName());
        em.persist(entity);

        SavedExpertSearchItem item = formSavedExpertSearchItem(entity);

        return item;
    }

    @Override
    public void removeExpertSearch(Long id) {
        SavedExpertSearchEntity entity = em.find(SavedExpertSearchEntity.class, id);
        em.remove(entity);
    }

    @Override
    public Pair<ReturnResult, SavedExpertSearchItem> getSavedExpertSearch(Long id) {
        SavedExpertSearchEntity entity = em.find(SavedExpertSearchEntity.class, id);
        return new ImmutablePair<>(entity == null ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, formSavedExpertSearchItem(entity));
    }

    private SavedExpertSearchItem formSavedExpertSearchItem(SavedExpertSearchEntity entity) {
        if(entity == null) {
            return null;
        }
        SavedExpertSearchItem item = new SavedExpertSearchItem();
        item.setId(entity.getId());
        item.setTitle(entity.getTitle());
        item.setQuery(entity.getQuery());
        item.setSavedAt(entity.getSavedAt().toString());
        item.setSavedBy(entity.getSavedBy());
        return item;
    }
}
