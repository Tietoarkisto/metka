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

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.BinderPageEntity;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.ConversionUtil;
import fi.uta.fsd.metka.transfer.binder.BinderPageListEntry;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BinderRepositoryImpl implements BinderRepository {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private StudySearch studies;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, BinderPageListEntry> saveBinderPage(SaveBinderPageRequest request) {
        Pair<ReturnResult, RevisionData> pair = studies.getLatestRevisionWithStudyId(request.getStudyId());
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            return new ImmutablePair<>(pair.getLeft(), null);
        }
        RevisionData study = pair.getRight();

        ReturnResult result = null;
        BinderPageEntity page = null;
        if(request.getPageId() != null) {
            page = em.find(BinderPageEntity.class, request.getPageId());
        }
        if(page == null) {
            page = new BinderPageEntity();
            try {
                page.setBinderId(ConversionUtil.stringToLong(request.getBinderId()));
            } catch(NumberFormatException e) {
                return new ImmutablePair<>(ReturnResult.INCORRECT_TYPE_FOR_OPERATION, null);
            }
            page.setStudy(study.getKey().getId());
            em.persist(page);
            result = ReturnResult.PAGE_CREATED;
        }
        page.setDescription(request.getDescription());
        page.setSavedAt(new LocalDateTime());
        page.setSavedBy(AuthenticationUtil.getUserName());

        BinderPageListEntry entry = formPageListEntry(study, page);

        return new ImmutablePair<>(result == null ? ReturnResult.PAGE_UPDATED : result, entry);
    }

    @Override
    public ReturnResult removePage(Long pageId) {
        em.remove(em.find(BinderPageEntity.class, pageId));
        return ReturnResult.PAGE_REMOVED;
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> listStudyBinderPages(Long study) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.study=:study ORDER BY p.pageId ASC", BinderPageEntity.class)
                .setParameter("study", study)
                .getResultList();
        List<BinderPageListEntry> pages = new ArrayList<>();
        for(BinderPageEntity entity : entities) {
            BinderPageListEntry page = new BinderPageListEntry();
            page.setPageId(entity.getPageId());
            page.setBinderId(entity.getBinderId());
            page.setDescription(entity.getDescription());
            pages.add(page);
        }
        return new ImmutablePair<>(pages.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, pages);
    }

    @Override
    public void removeStudyBinderPages(Long study) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.study=:study ORDER BY p.pageId ASC", BinderPageEntity.class)
                .setParameter("study", study)
                .getResultList();
        for(BinderPageEntity entity : entities) {
            em.remove(entity);
        }
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> listBinderPages() {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p ORDER BY p.binderId ASC", BinderPageEntity.class).getResultList();
        return formPageList(entities);
    }

    @Override
    public Pair<ReturnResult, List<BinderPageListEntry>> binderContent(Long binderId) {
        List<BinderPageEntity> entities = em.createQuery("SELECT p FROM BinderPageEntity p WHERE p.binderId=:binderId ORDER BY p.study ASC", BinderPageEntity.class)
                .setParameter("binderId", binderId)
                .getResultList();
        return formPageList(entities);
    }

    private Pair<ReturnResult, List<BinderPageListEntry>> formPageList(List<BinderPageEntity> entities) {
        List<BinderPageListEntry> pages = new ArrayList<>();
        for(BinderPageEntity entity : entities) {
            Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(entity.getStudy(), false, ConfigurationType.STUDY);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            pages.add(formPageListEntry(pair.getRight(), entity));
        }
        return new ImmutablePair<>(pages.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, pages);
    }

    private BinderPageListEntry formPageListEntry(RevisionData study, BinderPageEntity page) {
        BinderPageListEntry entry = new BinderPageListEntry();
        entry.setPageId(page.getPageId());
        entry.setBinderId(page.getBinderId());
        entry.setStudy(page.getStudy());
        Pair<StatusCode, ValueDataField> field = study.dataField(ValueDataFieldCall.get("studyid"));
        entry.setStudyId(field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));
        entry.setDescription(page.getDescription());
        entry.setSaved(new DateTimeUserPair(page.getSavedAt(), page.getSavedBy()));
        field = study.dataField(ValueDataFieldCall.get("title"));
        entry.setStudyTitle(field.getLeft() != StatusCode.FIELD_FOUND ? "" : field.getRight().getActualValueFor(Language.DEFAULT));
        return entry;
    }
}
