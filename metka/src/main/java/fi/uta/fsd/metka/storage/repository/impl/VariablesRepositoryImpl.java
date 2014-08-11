package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariableEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyVariablesEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.RevisionCreationRepository;
import fi.uta.fsd.metka.storage.repository.VariablesRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class VariablesRepositoryImpl implements VariablesRepository {
    private static Logger logger = LoggerFactory.getLogger(VariablesRepositoryImpl.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private JSONUtil json;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private GeneralRepository general;

    @Override
    public Pair<ReturnResult, RevisionData> getVariablesByStudyId(Long id) {
        // We can assume that id belongs to a study. If it doesn't then we won't find any variables or if we do there's something else wrong and we can't do anything about it here anyway.

        List<StudyVariablesEntity> entities = em.createQuery ("SELECT v FROM StudyVariablesEntity v WHERE v.studyId=:studyId", StudyVariablesEntity.class)
                .setParameter("studyId", id)
                .getResultList();
        if(entities.size() > 1) {
            // There should be only one STUDY_VARIABLES object per study id
            logger.error("There's more than one variables instance for given study with id "+id);
            return new ImmutablePair<>(ReturnResult.DATABASE_DISCREPANCY, null);
        } else if(entities.size() == 0) {
            // No variables found for given study
            return new ImmutablePair<>(ReturnResult.REVISION_NOT_FOUND, null);
        }

        // We have a variables entity
        return general.getLatestRevisionForIdAndType(entities.get(0).getId(), false, ConfigurationType.STUDY_VARIABLES);
    }

    @Override
    public Pair<ReturnResult, RevisionData> getEditableVariablesData(Long id) {
        StudyVariablesEntity variables = em.find(StudyVariablesEntity.class, id);
        if(variables == null) {
            logger.info("No study variables with id "+id+" found. New variables object can only be created through file parsing.");
            return null;
        }
        if(!canMakeDraftRevision(variables.getStudyId())) {
            // User is not allowed to make an editable study variables for some reason.
            // Specific reason should be determined by canMadeDraftRevision method
            return null;
        }
        if(variables.getLatestRevisionNo() == null) {
            logger.error("No revision marked for "+variables.toString());
            return null;
        }
        RevisionEntity revision = em.find(RevisionEntity.class, variables.latestRevisionKey());
        if(StringUtils.isEmpty(revision.getData())) {
            logger.error("Data for "+revision.toString()+" was empty");
            return null;
        }
        if(revision.getState() == RevisionState.DRAFT && variables.getCurApprovedNo() != null && variables.getCurApprovedNo().equals(variables.getLatestRevisionNo())) {
            logger.error("Latest revision for "+variables.toString()+" was in DRAFT state but revision numbers in entity indicate that latest revision should be APPROVED");
            return null;
        }
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        RevisionData data = json.deserializeRevisionData(revision.getData()).getRight();
        if(data == null) {
            return null;
        }
        if(revision.getState() == RevisionState.APPROVED) {
            // We need a new variables revision and are allowed to create one
            // TODO: Create new variables revision
            // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
            Configuration config = configurations.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES).getRight();
            // TODO: Use revision edit repository
            revision = new RevisionEntity(new RevisionKey(variables.getId(), variables.getLatestRevisionNo()+1));
            data = DataFactory.createDraftRevision(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), data, config.getKey());
            // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
            revision.setData(json.serialize(data).getRight());
            em.persist(revision);
            variables.setLatestRevisionNo(revision.getKey().getRevisionNo());
        }

        return new ImmutablePair<>(ReturnResult.REVISION_FOUND, data);
    }

    @Override
    public Pair<ReturnResult, RevisionData> getEditableVariableData(Long id) {
        StudyVariableEntity variable = em.find(StudyVariableEntity.class, id);
        if(variable == null) {
            logger.info("No study variable with id " + id + " found. New variable object can only be created through file parsing.");
            return null;
        }
        if(!canMakeDraftRevision(variable.getStudyId())) {
            // User is not allowed to make an editable study variables for some reason.
            // Specific reason should be determined by canMadeDraftRevision method
            return null;
        }
        if(variable.getLatestRevisionNo() == null) {
            logger.error("No revision marked for "+variable.toString());
            return null;
        }
        RevisionEntity revision = em.find(RevisionEntity.class, variable.latestRevisionKey());
        if(StringUtils.isEmpty(revision.getData())) {
            logger.error("Data for "+revision.toString()+" was empty");
            return null;
        }
        if(revision.getState() == RevisionState.DRAFT && variable.getCurApprovedNo() != null && variable.getCurApprovedNo().equals(variable.getLatestRevisionNo())) {
            logger.error("Latest revision for "+variable.toString()+" was in DRAFT state but revision numbers in entity indicate that latest revision should be APPROVED");
            return null;
        }
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        RevisionData data = json.deserializeRevisionData(revision.getData()).getRight();
        if(data == null) {
            return null;
        }
        if(revision.getState() == RevisionState.APPROVED) {
            // We need a new variables revision and are allowed to create one
            // TODO: Create new variables revision
            // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
            Configuration config = configurations.findLatestConfiguration(ConfigurationType.STUDY_VARIABLES).getRight();
            // TODO: Use revision edit repository
            revision = new RevisionEntity(new RevisionKey(variable.getId(), variable.getLatestRevisionNo()+1));
            data = DataFactory.createDraftRevision(revision.getKey().getRevisionableId(), revision.getKey().getRevisionNo(), data, config.getKey());
            // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
            revision.setData(json.serialize(data).getRight());
            em.persist(revision);
            variable.setLatestRevisionNo(revision.getKey().getRevisionNo());
        }

        return new ImmutablePair<>(ReturnResult.REVISION_FOUND, data);
    }

    /**
     * Checks to see if the user is permitted to create or edit a draft revision of STUDY_VARIABLES or STUDY_VARIABLE
     * // TODO: forward the actual fail cause to user
     * @param studyId
     * @return
     */
    private boolean canMakeDraftRevision(Long studyId) {
        StudyEntity study = em.find(StudyEntity.class, studyId);
        if(study == null) {
            logger.error("No study found with id "+studyId+" when determining DRAFT potential");
            return false;
        }
        if(study.getCurApprovedNo() != null && study.getLatestRevisionNo().equals(study.getCurApprovedNo())) {
            logger.info("Can't make new draft since study with id "+studyId+" is not in DRAFT state according to revision numbers");
            return false;
        }
        // There should be a draft but check just to make sure
        RevisionEntity revision = em.find(RevisionEntity.class, study.latestRevisionKey());
        if(revision == null) {
            logger.error("Couldn't find latest revision for study "+studyId);
            return false;
        }
        if(revision.getState() != RevisionState.DRAFT) {
            logger.error("Study with id "+studyId+" should have a draft but latest revision is not in DRAFT state");
            return false;
        }
        if(StringUtils.isEmpty(revision.getData())) {
            logger.error(revision.toString()+" has empty data");
            return false;
        }
        Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(revision.getData());
        if(pair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
            return false;
        }
        if(pair.getRight().getState() != RevisionState.DRAFT) {
            logger.error("Data for "+revision.toString()+" was not in DRAFT state even though is should have been.");
            return false;
        }
        // TODO: Check handler
        return true;
    }


}
