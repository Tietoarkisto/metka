package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.FileLinkQueueEntity;
import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.entity.key.RevisionKey;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.data.variableParsing.StudyVariablesParser;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

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
    public RevisionData getNew(Integer acquisition_number) throws IOException {
        StudyEntity entity = new StudyEntity();
        // Insert a new study number
        entity.setStudyNumber(general.getNewSequenceValue(ConfigurationType.STUDY.toValue(), 10000).getSequence());
        em.persist(entity);

        RevisionEntity revision = entity.createNextRevision();
        RevisionData data = factory.newData(revision, entity.getStudyNumber(), acquisition_number);
        em.persist(revision);

        entity.setLatestRevisionNo(revision.getKey().getRevisionNo());

        return data;
    }

    @Override
    // TODO: needs better reporting to user about what went wrong
    public boolean saveStudy(TransferObject to) throws IOException {
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

        // We can assume there is going to be latest revision since it is always required to exist.
        if(study.getCurApprovedNo() != null && study.getCurApprovedNo().equals(study.getLatestRevisionNo())) {
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
        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
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
            data.setLastSave(new LocalDateTime());
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
    public boolean approveStudy(Object studyno) throws IOException {
        StudyEntity study = em.find(StudyEntity.class, studyno);

        if(study == null) {
            // TODO: log suitable error
            return false;
        }

        if(study.getCurApprovedNo() == null && study.getLatestRevisionNo() == null) {
            // TODO: log suitable error
            System.err.println("No revision found when approving study "+studyno);
            return false;
        }

        if(study.getCurApprovedNo() != null && study.getCurApprovedNo().equals(study.getLatestRevisionNo())) {
            // Assume no DRAFT exists in this case. Add confirmation if necessary but it will still be an exception and
            // approval will not be done anyway.
            return true;
        }

        if(study.getCurApprovedNo() != null && study.getCurApprovedNo().compareTo(study.getLatestRevisionNo()) > 0) {
            // TODO: log exception since data is out of sync
            System.err.println("Current approved is larger than latest revision on study "+studyno+". This should not happen.");
            return false;
        }

        RevisionEntity entity = em.find(RevisionEntity.class, study.latestRevisionKey());
        if(entity.getState() != RevisionState.DRAFT) {
            // TODO: log exception since data is out of sync
            System.err.println("Latest revision should be DRAFT but is not on study "+studyno);
            return false;
        }

        RevisionData data = json.readRevisionDataFromString(entity.getData());

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

        // TODO: Make setting aipcomplete better
        if(data.getField("aipcomplete") == null) {
            SavedDataField aip = new SavedDataField("aipcomplete");
            aip.setModifiedValue(setSimpleValue(createSavedValue(new LocalDateTime()), new LocalDate().toString()));
            data.putField(aip);
            data.putChange(new Change("aipcomplete"));
        }

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
    public RevisionData editStudy(Object studyno) throws IOException {
        StudyEntity study = em.find(StudyEntity.class, studyno);

        if(study == null) {
            // TODO: log suitable error
            return null;
        }
        if(study.getCurApprovedNo() == null && study.getLatestRevisionNo() == null) {
            // TODO: log suitable error
            System.err.println("No revision found when trying to edit study "+studyno);
            return null;
        }

        RevisionEntity latestRevision = em.find(RevisionEntity.class, study.latestRevisionKey());
        RevisionData oldData = json.readRevisionDataFromString(latestRevision.getData());
        if(study.getCurApprovedNo() == null || study.getCurApprovedNo().compareTo(study.getLatestRevisionNo()) < 0) {
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


    @Override
    public void checkFileLinkQueue(Integer id, Integer revision) throws IOException {
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, id);
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(revisionable == null || ConfigurationType.valueOf(revisionable.getType()) != ConfigurationType.STUDY || entity.getState() != RevisionState.DRAFT) {
            // id/revision doesn't match STUDY DRAFT revision, don't do checks
            return;
        }

        RevisionData data = json.readRevisionDataFromString(entity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());
        LocalDateTime time = new LocalDateTime();
        boolean changes = false;

        // Check for existing FileLinkQueue events
        List<FileLinkQueueEntity> events = em
                .createQuery("SELECT e FROM FileLinkQueueEntity e WHERE e.targetId=:id", FileLinkQueueEntity.class)
                .setParameter("id", id)
                .getResultList();

        // For each FileLinkQueueEntity
        for(FileLinkQueueEntity event : events) {
            Field field = config.getField(event.getTargetField());
            // Sanity check, should field actually exist in this data, is it not a REFERENCECONTAINER or is it a subfield (handle those somewhere else
            if(field == null || field.getType() != FieldType.REFERENCECONTAINER || field.getSubfield()) {
                // Field should not exist, is not a REFERENCECONTAINER or is a subfield, can't add reference.
                continue;
            }

            // Check that references are found from data
            ReferenceContainerDataField references = getReferenceContainerDataFieldFromRevisionData(data, event.getTargetField(), config);
            if(references == null) { // Missing REFERENCECONTAINER, add container
                references = new ReferenceContainerDataField(event.getTargetField());
            }
            boolean found = false;
            for(SavedReference reference : references.getReferences()) {
                if(!reference.hasValue()) {
                    continue;
                }
                if(reference.valueEquals(event.getFileId().toString())) {
                    found = true;
                    break;
                }
            }
            // Reference was not found
            if(!found) {
                // Add missing reference
                SavedReference reference = new SavedReference(event.getTargetField(), data.getNewRowId());
                reference.setModifiedValue(setSimpleValue(createSavedValue(time), event.getFileId().toString()));
                references.getReferences().add(reference);
                changes = true;
                // Put references to data just in case it was not already there.
                data.putField(references);
            }
            // Check for variable file
            if(event.getType() != null) {
                // Check for variable file reference
                SavedDataField refField = getSavedDataFieldFromRevisionData(data, "variablefile", config);
                // If missing a reference then create new variablefile saved data field
                if(refField == null) {
                    refField = new SavedDataField("variablefile");
                    data.putField(refField);
                    changes = true;
                }
                // If no saved reference value then set event.fileId as reference value
                if(!refField.hasValue()) {
                    refField.setModifiedValue(setSimpleValue(createSavedValue(time), event.getFileId().toString()));
                    changes = true;
                }
                // If saved value matches event fileId then perform a merge parse operation otherwise variable file is not parsed
                if(refField.getActualValue().equals(event.getFileId().toString())) {
                    // TODO: Use new variables parser
                    changes = variableParser.merge(data, event.getType(), config) | changes;
                }
            }
        }

        // Save RevisionData back to DB
        if(changes) {
            entity.setData(json.serialize(data));
        }

        // Remove FileLinkQueue events
        em.createQuery("DELETE FROM FileLinkQueueEntity e WHERE e.targetId=:id")
                .setParameter("id", id)
                .executeUpdate();
    }
}