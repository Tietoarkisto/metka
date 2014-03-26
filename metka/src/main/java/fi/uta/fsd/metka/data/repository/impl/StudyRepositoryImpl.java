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
import fi.uta.fsd.metka.data.repository.StudyRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.data.util.StudyVariablesParser;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.model.data.value.SimpleValue;
import fi.uta.fsd.metka.model.factories.StudyFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import spssio.por.PORFile;
import spssio.por.input.PORReader;

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

    @Override
    public RevisionData getNew(Integer acquisition_number) throws IOException {
        StudyEntity entity = new StudyEntity();
        em.persist(entity);

        RevisionEntity revision = entity.createNextRevision();
        revision.setState(RevisionState.DRAFT);

        /*
         * creates initial dataset for the first draft any exceptions thrown should force rollback
         * automatically.
         * This assumes the entity has empty data field and is a draft.
        */
        RevisionData data = factory.newData(revision, acquisition_number);
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
            // There has to be a series so you can save
            return false;
        }

        // We can assume there is going to be latest revision since it is always required to exist.
        if(study.getCurApprovedNo() != null && study.getCurApprovedNo().equals(study.getLatestRevisionNo())) {
            // If latest revision is the same as current approved revision then it is not a draft and can not be saved
            return false;
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, new RevisionKey(study.getId(), study.getLatestRevisionNo()));
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

        DateTime time = new DateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        // If there were changes:
        // Serialize RevisionData.
        // Add revision data to entity.
        // Entity should still be managed at this point so

        if(changes) {
            data.setLastSave(new DateTime());
            revEntity.setData(json.serialize(data));
        }

        return true;
    }

    // TODO: During approval set aipcomplete if it's not set already. This is a value that should be set during first approval and not before.

    @Override
    public void checkFileLinkQueue(Integer id, Integer revision) throws IOException {
        RevisionableEntity revisionable = em.find(RevisionableEntity.class, id);
        RevisionEntity entity = em.find(RevisionEntity.class, new RevisionKey(id, revision));
        if(ConfigurationType.fromValue(revisionable.getType()) != ConfigurationType.STUDY || entity.getState() != RevisionState.DRAFT) {
            // id/revision doesn't match STUDY DRAFT revision, don't do checks
            return;
        }

        RevisionData data = json.readRevisionDataFromString(entity.getData());
        Configuration config = configRepo.findConfiguration(data.getConfiguration());
        DateTime time = new DateTime();
        boolean changes = false;

        // Check for existing FileLinkQueue events
        List<FileLinkQueueEntity> events = em
                .createQuery("SELECT e FROM FileLinkQueueEntity e WHERE e.targetId=:id", FileLinkQueueEntity.class)
                .setParameter("id", id)
                .getResultList();

        // For each FileLinkQueueEntity
        PORReader porReader = new PORReader();
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
            // Check for POR-files
            changes = StudyVariablesParser.merge(data, event.getType(), event.getPath(), config) | changes;
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