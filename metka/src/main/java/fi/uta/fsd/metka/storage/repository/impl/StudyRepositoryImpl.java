package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.StudyAttachmentQueueEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.StudyRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static fi.uta.fsd.metka.storage.util.ModelAccessUtil.*;

@Repository
public class StudyRepositoryImpl implements StudyRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private StudyFactory factory;

    @Autowired
    private JSONUtil json;

    @Autowired
    private ConfigurationRepository configRepo;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private StudyVariablesParser variableParser;

    @Override
    public RevisionData getNew(Long acquisition_number) {
        StudyEntity entity = new StudyEntity();
        // Insert a new study number
        entity.setStudyNumber(general.getNewSequenceValue(ConfigurationType.STUDY.toValue(), 10000L).getSequence());
        em.persist(entity);

        RevisionEntity revision = entity.createNextRevision();
        RevisionData data = factory.newData(revision, entity.getStudyNumber(), acquisition_number);
        em.persist(revision);

        entity.setLatestRevisionNo(revision.getKey().getRevisionNo());

        return data;
    }

    @Override
    // TODO: needs better reporting to user about what went wrong
    public boolean saveStudy(TransferObject to) {
        // Get StudyEntity
        // Check if latest revision is different from latest approved (the first requirement since only drafts
        // can be saved and these should always be different if Revisionable has an active draft).
        // Get latest revision.
        // Check state
        // Deserialize revision data and check that data agrees with entity.

        StudyEntity study = em.find(StudyEntity.class, to.getId());
        if(study == null) {
            // There has to be a study so you can save
            return false;
        }

        if(!study.hasDraft()) {
            // If latest revision is the same as current approved revision then it is not a draft and can not be saved
            return false;
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, study.latestRevisionKey());
        if(revEntity.getState() != RevisionState.DRAFT || StringUtils.isEmpty(revEntity.getData())) {
            // Only drafts can be saved and there has to be existing revision data
            // TODO: if we get here an error has to be logged since data is out of sync
            return false;
        }

        // Get old revision data and its configuration.
        RevisionData data = json.deserializeRevisionData(revEntity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());

        // Validate StudySingleSO against revision data:
        // Id should match id in revision data and key.
        // Revision should match revision in key
        // Study_number should match that of existing revision since it's set at first creation.

        // Check ID integrity
        if (idIntegrityCheck(to, data, config)) {
            return false;
        }

        // check revision
        if(!to.getRevision().equals(data.getKey().getRevision())) {
            // TODO: data is out of sync or someone tried to change the revision, log error
            // Return false since save can not continue.
            return false;
        }

        // Check values

        boolean changes = false;

        LocalDateTime time = new LocalDateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            data.setLastSaved(new LocalDateTime());
            // TODO: Set last saved by
            revEntity.setData(json.serialize(data));
        }

        return true;
    }

    /*
    * Approve Study.
    * Modifies the revision data so that it is a correct approved data set, changes state to APPROVED
    * and then saves it back to database.
    *
    */
    @Override
    public boolean approveStudy(Object studyno) {
        StudyEntity study = em.find(StudyEntity.class, studyno);

        if(study == null) {
            // TODO: log suitable error
            return false;
        }

        if(!study.hasDraft()) {
            // Assume no DRAFT exists in this case. Add confirmation if necessary but it will still be an exception and
            // approval will not be done anyway.
            return true;
        }

        RevisionEntity entity = em.find(RevisionEntity.class, study.latestRevisionKey());
        if(entity.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Latest revision should be DRAFT but is not on study "+studyno);
            return false;
        }

        RevisionData data = json.deserializeRevisionData(entity.getData());

        // Check that data is also in DRAFT state and that id and revision match.
        // For each change:
        //          If the operation is unchanged then take the original value.
        //          If the operation is removed then take no value.
        //          If the operation is modified take the new value.
        // TODO: Validate changed value where necessary

        if(data.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Revision data on study "+studyno+" was not in DRAFT state even though should have been.");
            return false;
        }

        if(!data.getKey().getId().equals(entity.getKey().getRevisionableId())
                || !data.getKey().getRevision().equals(entity.getKey().getRevisionNo())) {
            // TODO: log exception since data and entity keys don't match
            System.err.println("RevisionEntity and RevisionData keys do not match");
            System.err.println(data.getKey());
            System.err.println(entity.getKey());

            return false;
        }

        boolean dontApprove = false;

        // Try to approve all study attachments linked to this study (this should move files from temporary location to their actual location)

        // Try to approve study variables linked to this study

        // Try to approve each study variable linked to this study

        // TODO: check return value for errors
        data.dataField(SavedDataFieldCall.set("aipcomplete").setValue(new LocalDate().toString()));

        // Change state in revision data to approved.
        // Serialize data back to revision entity.
        // Change state of entity to approved.
        // Update current approved revision number on study entity
        // Entities should still be managed so no merge necessary.
        data.setState(RevisionState.APPROVED);
        data.setApprovalDate(new LocalDateTime());
        // TODO: set approver for the data to the user who requested the data approval
        entity.setData(json.serialize(data));
        entity.setState(RevisionState.APPROVED);
        study.setCurApprovedNo(study.getLatestRevisionNo());

        return true;
    }

    @Override
    public RevisionData editStudy(Object studyno) {
        StudyEntity study = em.find(StudyEntity.class, studyno);

        if(study == null) {
            // TODO: log suitable error
            return null;
        }

        RevisionEntity latestRevision = em.find(RevisionEntity.class, study.latestRevisionKey());
        RevisionData oldData = json.deserializeRevisionData(latestRevision.getData());
        if(study.hasDraft()) {
            if(latestRevision.getState() != RevisionState.DRAFT) {
                // TODO: log exception since data is out of sync
                System.err.println("Latest revision should be DRAFT but is not on study "+studyno);
                return null;
            }
            if(oldData.getState() != RevisionState.DRAFT) {
                // TODO: log exception since data is out of sync
                System.err.println("Revision data on study "+studyno+" was not in DRAFT state even though should have been.");
                return null;
            }
            // TODO: Check that handler is acceptable
            return oldData;
        }

        // If not then create new revision
        // Increase revision number from latest revision
        // Set state to DRAFT
        // Generate initial data
        // Get latest revision
        // Go through fields map
        // For each field generate change with operation UNCHANGED and put the field to original value
        // Add changes to new dataset
        RevisionEntity newRevision = study.createNextRevision();
        RevisionData newData = DataFactory.createNewRevisionData(newRevision, oldData);
        // TODO: Set data handler

        // Serialize new dataset to the new revision entity
        // Persist new entity
        newRevision.setData(json.serialize(newData));
        em.persist(newRevision);

        // Set latest revision number to new revisions revision number
        // No merge needed since entity still managed
        // Return new revision data
        study.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
        return newData;
    }

// TODO: Set last saved by
    @Override
    public void checkFileLinkQueue(Long id, Integer revision) {
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, id);
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(revisionable == null || ConfigurationType.valueOf(revisionable.getType()) != ConfigurationType.STUDY || entity.getState() != RevisionState.DRAFT) {
            // id/revision doesn't match STUDY DRAFT revision, don't do checks
            return;
        }

        RevisionData data = json.deserializeRevisionData(entity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());
        LocalDateTime time = new LocalDateTime();
        boolean changes = false;

        // Check for existing FileLinkQueue events
        List<StudyAttachmentQueueEntity> events = em
                .createQuery("SELECT e FROM StudyAttachmentQueueEntity e WHERE e.targetStudy=:study", StudyAttachmentQueueEntity.class)
                .setParameter("study", id)
                .getResultList();

        // For each FileLinkQueueEntity
        for(StudyAttachmentQueueEntity event : events) {
            // Check that references are found from data
            ReferenceContainerDataField references = data.dataField(ReferenceContainerDataFieldCall.set(event.getStudyAttachmentField()).setConfiguration(config)).getRight();
            if(references == null) {
                // Something went wrong
                // TODO: Log some kind of exception
                continue;
            }

            Pair<StatusCode, SavedReference> srPair = references.getOrCreateReferenceWithValue(event.getStudyAttachmentId().toString(), data.getChanges(), time);
            if(srPair.getRight() == null) {
                // Something wrong with getting or creating the reference, can't continue
                // TODO: Log error
                continue;
            }
            if(srPair.getLeft() == StatusCode.NEW_ROW) {
                changes = true;
            }

            // Check for variable file
            if(event.getType() != null) {
                // Check for variable file reference
                SavedDataField refField = data.dataField(SavedDataFieldCall.get("variablefile").setConfiguration(config)).getRight();
                if(refField != null && !refField.valueEquals(event.getStudyAttachmentId().toString())) {
                    // There is already a variable file with different id, don't parse this file as a variable file
                    continue;
                }
                // Try to set events study attachment id to variablefile field, then if successfull parse the file and notify of change
                Pair<StatusCode, SavedDataField> sdPair = data.dataField(SavedDataFieldCall.set("variablefile").setTime(time).setValue(event.getStudyAttachmentId().toString()).setConfiguration(config));
                if(sdPair.getRight() == null) {
                    // Setting the value was unsuccessful don't continue with parsing
                    // TODO: Log error
                    continue;
                }
                if(sdPair.getLeft() != StatusCode.NO_CHANGE_IN_VALUE) {
                    changes = true;
                }
                changes = variableParser.merge(data, event.getType(), config) | changes;
            }
        }

        // Save RevisionData back to DB
        if(changes) {
            entity.setData(json.serialize(data));
        }

        // Remove FileLinkQueue events
        em.createQuery("DELETE FROM StudyAttachmentQueueEntity e WHERE e.targetStudy=:study")
                .setParameter("study", id)
                .executeUpdate();
    }
}