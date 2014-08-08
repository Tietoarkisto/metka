package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.access.enums.StatusCode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.StudyAttachmentQueueEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.entity.key.RevisionKey;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.StudyRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.storage.variables.StudyVariablesParser;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class StudyRepositoryImpl {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    private StudyFactory factory;

    @Autowired
    private JSONUtil json;

    @Autowired
    private ConfigurationRepository configRepo;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private StudyVariablesParser variableParser;


    // TODO: This is done in the generalised revision edit
    /*@Override
    public RevisionData editStudy(Object studyno) {

        RevisionData oldData = json.deserializeRevisionData(latestRevision.getData()).getRight();
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
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        newRevision.setData(json.serialize(newData).getRight());
        em.persist(newRevision);

        // Set latest revision number to new revisions revision number
        // No merge needed since entity still managed
        // Return new revision data
        study.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
        return newData;
    }*/

    public void checkFileLinkQueue(Long id, Integer revision) {
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, id);
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(revisionable == null || ConfigurationType.valueOf(revisionable.getType()) != ConfigurationType.STUDY || entity.getState() != RevisionState.DRAFT) {
            // id/revision doesn't match STUDY DRAFT revision, don't do checks
            return;
        }

        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        RevisionData data = json.deserializeRevisionData(entity.getData()).getRight();
        Configuration config = configRepo.findConfiguration(data.getConfiguration()).getRight();
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
            // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
            entity.setData(json.serialize(data).getRight());
        }

        // Remove FileLinkQueue events
        em.createQuery("DELETE FROM StudyAttachmentQueueEntity e WHERE e.targetStudy=:study")
                .setParameter("study", id)
                .executeUpdate();
    }
}