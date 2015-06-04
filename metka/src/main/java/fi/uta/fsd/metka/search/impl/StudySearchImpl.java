package fi.uta.fsd.metka.search.impl;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.search.StudySearch;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.StudyErrorEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Repository
public class StudySearchImpl implements StudySearch {

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> getStudiesWithVariables() {
        List<StudyVariablesEntity> entities = em.createQuery("SELECT e FROM StudyVariablesEntity e", StudyVariablesEntity.class).getResultList();
        if(entities.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.NO_RESULTS, (List<RevisionSearchResult>)new ArrayList<RevisionSearchResult>());
        }
        List<RevisionSearchResult> results = new ArrayList<>();
        for(StudyVariablesEntity entity : entities) {
            Pair<ReturnResult, RevisionData> dataPair = revisions.getLatestRevisionForIdAndType(entity.getStudyVariablesStudy(), false, ConfigurationType.STUDY);
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            RevisionSearchResult result = new RevisionSearchResult();
            result.setId(entity.getId());
            Pair<StatusCode, ValueDataField> fieldPair = dataPair.getRight().dataField(ValueDataFieldCall.get("title"));
            String title = (fieldPair.getLeft() == StatusCode.FIELD_FOUND) ? fieldPair.getRight().getActualValueFor(Language.DEFAULT) : "[Aineistolla ei ole nimeä]";

            result.getValues().put("title", title);
            results.add(result);
        }
        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, results);
    }

    @Override
    public Pair<ReturnResult, RevisionData> getLatestRevisionWithStudyId(String studyId) {
        List<StudyEntity> studies = em.createQuery("SELECT s FROM StudyEntity s WHERE s.studyId=:studyId", StudyEntity.class)
                .setParameter("studyId", studyId)
                .getResultList();
        if(studies.isEmpty()) {
            return new ImmutablePair<>(ReturnResult.REVISIONABLE_NOT_FOUND, null);
        }

        StudyEntity study = studies.get(0);

        // Get the latest revision for study and, if it exists, get or create files reference container
        return revisions.getRevisionData(study.latestRevisionKey());
    }

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> collectAttachmentHistory(Long attachmentId) {
        List<RevisionSearchResult> results = new ArrayList<>();

        List<RevisionEntity> revisions = em.createQuery("SELECT r FROM RevisionEntity r WHERE r.key.revisionableId=:attachmentId", RevisionEntity.class)
                .setParameter("attachmentId", attachmentId)
                .getResultList();

        Pair<ReturnResult, RevisionableInfo> infoPair = this.revisions.getRevisionableInfo(attachmentId);
        if(infoPair.getLeft() != ReturnResult.REVISIONABLE_FOUND) {
            return new ImmutablePair<>(infoPair.getLeft(), results);
        }
        for(RevisionEntity revision : revisions) {
            Pair<ReturnResult, RevisionData> dataPair = this.revisions.getRevisionData(revision.getKey());
            if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
                Logger.error(getClass(), "Could not find revision for study attachment with key " + revision.getKey().toString());
                continue;
            }
            RevisionData data = dataPair.getRight();
            RevisionSearchResult result = RevisionSearchResult.build(data, infoPair.getRight());
            results.add(result);
            // TODO: If the file is removed then fetch the removal comment instead for the last revision.
            Pair<StatusCode, ValueDataField> fieldPair = data.dataField(ValueDataFieldCall.get("filecomment"));
            if(fieldPair.getLeft() == StatusCode.FIELD_FOUND && fieldPair.getRight().hasValueFor(Language.DEFAULT)) {
                result.getValues().put("filecomment", fieldPair.getRight().getActualValueFor(Language.DEFAULT));
            }
            if(data.getSaved() != null) {
                result.getValues().put("date", data.getSaved().getTime().toString("yyyy-MM-dd"));
                result.getValues().put("user", data.getSaved().getUser());
            }
        }

        return new ImmutablePair<>(results.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, results);
    }

    @Override
    public Pair<ReturnResult, List<RevisionSearchResult>> getStudiesWithErrors() {
        List<StudyErrorEntity> errors = em.createQuery("SELECT e FROM StudyErrorEntity e ORDER BY e.studyErrorStudy", StudyErrorEntity.class)
                .getResultList();

        List<RevisionSearchResult> results = new ArrayList<>();

        Map<Long, List<StudyErrorEntity>> groups = new HashMap<>();
        for(StudyErrorEntity error : errors) {
            if(!groups.containsKey(error.getStudyErrorStudy())) {
                groups.put(error.getStudyErrorStudy(), new ArrayList<StudyErrorEntity>());
            }
            groups.get(error.getStudyErrorStudy()).add(error);
        }

        for(Long id : groups.keySet()) {
            Long score = 0L;
            for(StudyErrorEntity error : groups.get(id)) {
                score += error.getErrorscore();
            }
            RevisionSearchResult result = new RevisionSearchResult();
            result.setId(id);
            result.setType(ConfigurationType.STUDY);
            result.getValues().put("score", score.toString());
            results.add(result);
        }

        Collections.sort(results, new Comparator<RevisionSearchResult>() {
            @Override
            public int compare(RevisionSearchResult o1, RevisionSearchResult o2) {
                return Long.compare(Long.parseLong(o2.getValues().get("score")), Long.parseLong(o1.getValues().get("score")));
            }
        });

        return new ImmutablePair<>(results.isEmpty() ? ReturnResult.NO_RESULTS : ReturnResult.OPERATION_SUCCESSFUL, results);
    }
}
