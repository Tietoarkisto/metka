package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudySearchImpl implements StudySearch {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private GeneralRepository general;

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> getStudiesWithVariables() {
        List<StudyVariablesEntity> entities = em.createQuery("SELECT e FROM StudyVariablesEntity e").getResultList();
        if(entities.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.NO_RESULTS, (List<RevisionSearchResult>)new ArrayList<RevisionSearchResult>());
        }
        List<RevisionSearchResult> results = new ArrayList<>();
        for(StudyVariablesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> study = general.getLatestRevisionForIdAndType(entity.getStudyId(),
                    false, ConfigurationType.STUDY);
            if(study.getLeft() != ReturnResult.REVISION_FOUND) {
                // TODO: Log error since there should be a revision
                continue;
            }
            Pair<StatusCode, SavedDataField> fieldPair = study.getRight().dataField(SavedDataFieldCall.get("title"));
            RevisionSearchResult result = new RevisionSearchResult();
            result.setId(entity.getId());
            result.getValues().put("title", (fieldPair.getLeft() == StatusCode.FIELD_FOUND ? fieldPair.getRight().getActualValue() : ""));
            results.add(result);
        }
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, results);
    }
}
