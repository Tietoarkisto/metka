package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.search.StudyVariableSearch;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class StudyVariableSearchImpl implements StudyVariableSearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, RevisionData> findVariableWithId(Long studyId, String variableId) {
        List<StudyVariableEntity> entities = em.createQuery("SELECT e FROM StudyVariableEntity e WHERE e.studyVariableStudy=:studyId AND e.varId=:variableId", StudyVariableEntity.class)
                .setParameter("studyId", studyId)
                .setParameter("variableId", variableId)
                .getResultList();

        if(entities.size() == 0) {
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_FOUND, null);
        } else if(entities.size() > 1) {
            Logger.error(getClass(), "More than one Study Variable found for study id " + studyId + " and variable id " + variableId);
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        }
        StudyVariableEntity entity = entities.get(0);

        return revisions.getLatestRevisionForIdAndType(entity.getId(), false, ConfigurationType.STUDY_VARIABLE);
    }
}
