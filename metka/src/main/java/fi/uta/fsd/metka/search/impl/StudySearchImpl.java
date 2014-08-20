package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudySearchImpl implements StudySearch {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> getStudiesWithVariables() {
        List<StudyVariablesEntity> entities = em.createQuery("SELECT e FROM StudyVariablesEntity e", StudyVariablesEntity.class).getResultList();
        if(entities.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.NO_RESULTS, (List<RevisionSearchResult>)new ArrayList<RevisionSearchResult>());
        }
        List<RevisionSearchResult> results = new ArrayList<>();
        for(StudyVariablesEntity entity : entities) {
            RevisionSearchResult result = new RevisionSearchResult();
            result.setId(entity.getId());
            result.getValues().put("title", entity.getStudyId());
            results.add(result);
        }
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, results);
    }
}
