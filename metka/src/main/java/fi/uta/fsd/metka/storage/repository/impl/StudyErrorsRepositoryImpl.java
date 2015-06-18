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

import fi.uta.fsd.metka.storage.entity.StudyErrorEntity;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metkaAmqp.Messenger;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudyErrorsRepositoryImpl implements StudyErrorsRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    private static final Integer THRESHOLD = 10;

    @Autowired
    private Messenger messenger;

    @Override
    public List<StudyError> listErrorsForStudy(Long studyId) {
        List<StudyErrorEntity> errors = em.createQuery(
                "SELECT e FROM StudyErrorEntity e WHERE e.studyErrorStudy=:studyId ORDER BY e.savedAt ASC",
                StudyErrorEntity.class)
                .setParameter("studyId", studyId)
                .getResultList();

        List<StudyError> result = new ArrayList<>();
        for(StudyErrorEntity entity : errors) {
            result.add(studyErrorFromEntity(entity));
        }

        return result;
    }

    @Override
    public void removeErrorsForStudy(Long studyId) {
        List<StudyErrorEntity> errors = em.createQuery(
                "SELECT e FROM StudyErrorEntity e WHERE e.studyErrorStudy=:studyId ORDER BY e.savedAt ASC",
                StudyErrorEntity.class)
                .setParameter("studyId", studyId)
                .getResultList();

        for(StudyErrorEntity error : errors) {
            em.remove(error);
        }
    }

    @Override
    public Pair<ReturnResult, StudyError> loadStudyError(Long id) {
        StudyErrorEntity error = em.find(StudyErrorEntity.class, id);
        if(error == null) {
            return new ImmutablePair<>(ReturnResult.NO_RESULTS, null);
        } else {
            return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, studyErrorFromEntity(error));
        }
    }

    @Override
    public ReturnResult updateStudyError(StudyError error) {
        StudyErrorEntity entity;
        if(error.getId() == null) {
            entity = new StudyErrorEntity();
            entity.setStudyErrorStudy(error.getStudyId());
            entity.setSavedAt(new LocalDateTime());
            entity.setSavedBy(AuthenticationUtil.getUserName());
            em.persist(entity);
        } else {
            entity = em.find(StudyErrorEntity.class, error.getId());
            if(entity == null) {
                return ReturnResult.NO_RESULTS;
            }
        }
        updateStudyErrorEntity(entity, error);
        em.merge(entity);

        List<StudyErrorEntity> entities = em.createQuery("SELECT e FROM StudyErrorEntity e WHERE e.studyErrorStudy=:study", StudyErrorEntity.class)
                .setParameter("study", error.getStudyId())
                .getResultList();

        Integer points = 0;
        for(StudyErrorEntity e : entities) {
            points += e.getErrorscore();
        }
        if(points >= THRESHOLD) {
            // TODO: Check how to decide the trigger recipient and where to send it.
            messenger.sendAmqpMessage(Messenger.AmqpMessageType.STUDY_ERROR_POINTS_OVER_TRESHOLD);
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }

    @Override
    public ReturnResult deleteStudyError(Long id) {
        StudyErrorEntity entity = em.find(StudyErrorEntity.class, id);
        if(entity != null) {
            em.remove(entity);
            return ReturnResult.OPERATION_SUCCESSFUL;
        } else {
            return ReturnResult.NO_RESULTS;
        }
    }

    private StudyError studyErrorFromEntity(StudyErrorEntity entity) {
        StudyError error = new StudyError();
        error.setId(entity.getId());
        error.setStudyId(entity.getStudyErrorStudy());
        error.setSavedAt(entity.getSavedAt());
        error.setSavedBy(entity.getSavedBy());
        error.setErrorscore(entity.getErrorscore());
        error.setErrordatasetpart(entity.getErrordatasetpart());
        error.setErrorpartsection(entity.getErrorpartsection());
        error.setErrorlanguage(entity.getErrorlanguage());
        error.setErrorlabel(entity.getErrorlabel());
        error.setErrornotes(entity.getErrornotes());
        error.setErrortriggerdate(entity.getErrortriggerdate());
        error.setErrortriggerpro(entity.getErrortriggerpro());
        return error;
    }

    private void updateStudyErrorEntity(StudyErrorEntity entity, StudyError error) {
        entity.setErrorscore(error.getErrorscore());
        entity.setErrordatasetpart(error.getErrordatasetpart());
        entity.setErrorpartsection(error.getErrorpartsection());
        entity.setErrorlanguage(error.getErrorlanguage());
        entity.setErrorlabel(error.getErrorlabel());
        entity.setErrornotes(error.getErrornotes());
        entity.setErrortriggerdate(error.getErrortriggerdate());
        entity.setErrortriggerpro(error.getErrortriggerpro());
    }
}
