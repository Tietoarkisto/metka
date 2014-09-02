package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.StudyErrorEntity;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudyErrorsRepositoryImpl implements StudyErrorsRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public List<StudyError> listErrorsForStudy(Long studyId) {
        List<StudyErrorEntity> errors = em.createQuery(
                "SELECT e FROM StudyErrorEntity e WHERE e.studyId=:studyId ORDER BY e.addedAt ASC",
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
            entity.setStudyId(error.getStudyId());
            entity.setAddedAt(new LocalDateTime());
            entity.setAddedBy(AuthenticationUtil.getUserName());
            em.persist(entity);
        } else {
            entity = em.find(StudyErrorEntity.class, error.getId());
            if(entity == null) {
                return ReturnResult.NO_RESULTS;
            }
        }
        updateStudyErrorEntity(entity, error);
        em.merge(entity);
        // TODO: Calculate total score and send notification if too high
        // TODO: Send notification with trigger date and target
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
        error.setAddedAt(entity.getAddedAt());
        error.setAddedBy(entity.getAddedBy());
        error.setScore(entity.getScore());
        error.setSection(entity.getSection());
        error.setSubsection(entity.getSubsection());
        error.setLanguage(entity.getLanguage());
        error.setSummary(entity.getSummary());
        error.setDescription(entity.getDescription());
        error.setTriggerDate(entity.getTriggerDate());
        error.setTriggerTarget(entity.getTriggerTarget());
        return error;
    }

    private void updateStudyErrorEntity(StudyErrorEntity entity, StudyError error) {
        entity.setScore(error.getScore());
        entity.setSection(error.getSection());
        entity.setSubsection(error.getSubsection());
        entity.setLanguage(error.getLanguage());
        entity.setSummary(error.getSummary());
        entity.setDescription(error.getDescription());
        entity.setTriggerDate(error.getTriggerDate());
        entity.setTriggerTarget(error.getTriggerTarget());
    }
}
