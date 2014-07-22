package fi.uta.fsd.metka.data.repository.impl;

import fi.uta.fsd.metka.data.entity.RevisionEntity;
import fi.uta.fsd.metka.data.entity.RevisionableEntity;
import fi.uta.fsd.metka.data.entity.StudyAttachmentQueueEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyAttachmentEntity;
import fi.uta.fsd.metka.data.entity.impl.StudyEntity;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.data.enums.VariableDataType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metka.data.repository.StudyAttachmentRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.FileFactory;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.List;

import static fi.uta.fsd.metka.data.util.ModelAccessUtil.*;

@Repository
public class StudyAttachmentRepositoryImpl implements StudyAttachmentRepository {
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private FileFactory factory;

    @Autowired
    private ConfigurationRepository configRepo;

    @Autowired
    private GeneralRepository general;

    @Autowired
    private JSONUtil json;

    /**
     * Gets RevisionData for study attachment with given path.
     * If no existing study attachment is found then new attachment is created.
     * Due to inability to process through information that is not yet saved to database a full search
     * has to be performed to check whether study attachment exists.
     *
     * @param path Path that should be present in the study attachment
     * @return RevisionData for a study attachment
     * @throws IOException
     */
    @Override
    public RevisionData studyAttachmentForPath(String path, Long studyId) throws IOException {
        // Get all study attachments
        List<StudyAttachmentEntity> attachments = em.
                createQuery("SELECT r FROM StudyAttachmentEntity r WHERE r.removed = false AND r.filePath=:path", StudyAttachmentEntity.class)
                .setParameter("path", path)
                .getResultList();

        // Go through attachments checking their latest revision
        // If there is a 'file' field and if the value in that field matches given path then break
        RevisionData revision = null;
        RevisionEntity revisionEntity = null;
        for(StudyAttachmentEntity attachment : attachments) {
            revisionEntity = em.
                    find(RevisionEntity.class, attachment.latestRevisionKey());
            revision = json.readRevisionDataFromString(revisionEntity.getData());
            SavedDataField fileField = revision.dataField(SavedDataFieldCall.get("file")).getRight();
            if(fileField != null && fileField.hasValue() && fileField.getActualValue().equals(path)) {
                // Path match found
                break;
            } else if(fileField != null && fileField.hasValue()) {
                // Update path to latest
                attachment.setFilePath(fileField.getActualValue());
            }
            revision = null;
        }

        // No previous study attachment with path found
        // Create new attachment with initial revision
        if(revision == null) {
            StudyAttachmentEntity entity = new StudyAttachmentEntity();
            em.persist(entity);

            revisionEntity = entity.createNextRevision();

            /*
             * creates initial data set for the first draft any exceptions thrown should force rollback
             * automatically.
             * This assumes the entity has empty data field and is a draft.
            */
            revision = factory.newStudyAttachmentData(revisionEntity, path, studyId);
            entity.setFilePath(path);
            em.persist(revisionEntity);

            entity.setLatestRevisionNo(revisionEntity.getKey().getRevisionNo());
        } else {
            // Check that this revision belongs to given study
            SavedDataField studyField = revision.dataField(SavedDataFieldCall.get("study")).getRight();
            if(studyField == null || !studyField.hasValue() || !studyField.getActualValue().equals(studyId.toString())) {
                // TODO: Attachment exists but it's marked for some other study, log exception, we can't continue
                return null;
            }
        }

        return revision;
    }


    @Override
    public RevisionData getEditableStudyAttachmentRevision(Long id) throws IOException {
        RevisionableEntity file = em.find(RevisionableEntity.class, id);

        // Sanity check
        if(file == null || ConfigurationType.valueOf(file.getType()) != ConfigurationType.STUDY_ATTACHMENT || file.getLatestRevisionNo() == null) {
            // TODO: Log error, this should never happen.
            return null;
        }

        RevisionEntity revision = em.find(RevisionEntity.class, file.latestRevisionKey());
        if(revision == null) {
            // TODO: Missing revision, log error
            return null;
        }
        RevisionData data = json.readRevisionDataFromString(revision.getData());
        if(data == null) {
            // TODO: Log error, missing data
            return null;
        }
        // If DRAFT exists, return it
        if(file.hasDraft()) {
            // Sanity check that latest revision is indeed a DRAFT
            if(revision.getState() != RevisionState.DRAFT || data.getState() != RevisionState.DRAFT) {
                // TODO: Log error, there is a data discrepancy
                return null;
            }

            return data;
        } else {
            // Latest revision should be APPROVED, make sanity check and if passed create a new DRAFT
            if(revision.getState() != RevisionState.APPROVED || data.getState() != RevisionState.APPROVED) {
                // TODO: Log error, there is a data discrepancy
                return null;
            }

            RevisionEntity newRevision = file.createNextRevision();
            em.persist(newRevision);
            file.setLatestRevisionNo(newRevision.getKey().getRevisionNo());

            // Get latest configuration for configuration key for new data
            Configuration config = configRepo.findLatestConfiguration(ConfigurationType.STUDY_ATTACHMENT);

            // Create new RevisionData object using the current revEntity (either new or old, doesn't matter)
            RevisionData newData = DataFactory.createNewRevisionData(newRevision, data, config.getKey());
            // TODO: Set handler

            newRevision.setData(json.serialize(newData));

            return newData;
        }
    }

    @Override
    public void studyAttachmentSaveAndApprove(TransferObject to) throws Exception {
        StudyAttachmentEntity file = em.find(StudyAttachmentEntity.class, to.getId());
        if(file == null) {
            // There has to be a file so you can save
            throw new Exception("No file found for id "+to.getId());
        }

        // Sanity check
        if(file.getLatestRevisionNo() == null) {
            // TODO: There's no latest revision, something is really wrong, log error.
            throw new Exception("No revision found for id "+to.getId());
        }

        RevisionEntity revEntity = em.find(RevisionEntity.class, file.latestRevisionKey());
        if(revEntity == null) {
            // TODO: Log error, revision should excist
            throw new Exception("No revision found for id "+to.getId() + " and revision "+file.getLatestRevisionNo());
        }
        if(revEntity.getState() != RevisionState.DRAFT) {
            // TODO: Log error, revision should be a draft
            throw new Exception("Revision is not a DRAFT");
        }

        RevisionData data = json.readRevisionDataFromString(revEntity.getData());
        Configuration config = configRepo.findLatestConfiguration(ConfigurationType.STUDY_ATTACHMENT);

        // Latest data is not a DRAFT
        if(data.getState() != RevisionState.DRAFT) {
            // TODO: Log error, data discrepancy
            throw new Exception("Data is not a DRAFT");
        }

        if (idIntegrityCheck(to, data, config)) {
            throw new Exception("Id integrity was not maintained");
        }

        // Check values

        boolean changes = false;

        LocalDateTime time = new LocalDateTime();

        for(Field field : config.getFields().values()) {
            changes = doFieldChanges(field.getKey(), to, time, data, config) | changes;
        }

        // TODO: Do CONCAT checking

        if(changes) {
            // If there were changes save and approve current revision.
            data.setState(RevisionState.APPROVED);
            data.setApprovalDate(new LocalDateTime());
            data.setLastSaved(new LocalDateTime());
            // TODO: set last saved by

            revEntity.setData(json.serialize(data));
            revEntity.setState(RevisionState.APPROVED);
            file.setCurApprovedNo(revEntity.getKey().getRevisionNo());
        }
    }

    @Override
    public void addFileLinkEvent(Long studyId, Long fileId, String key, String path) throws IOException {
        StudyEntity study = em.find(StudyEntity.class, studyId);
        if(study == null) {
            // No study with given id, nothing to attach to
            return;
        }
        StudyAttachmentQueueEntity queue = new StudyAttachmentQueueEntity();
        queue.setTargetStudy(studyId);
        queue.setStudyAttachmentId(fileId);
        queue.setStudyAttachmentField(key);
        queue.setPath(path);
        // Check if target is study

        queue.setType(null);
        boolean parse = true;
        // Check if file is variable file name
        if(parse) {
            if(!FilenameUtils.getName(path).substring(0, 3).toUpperCase().equals("DAF")) {
                // Variable file names need to start with DAF
                parse = false;
            }
        }
        // Check if file is variable file based on file extension
        if(parse) {
            if(!FilenameUtils.getExtension(path).toUpperCase().equals("POR")) {
                // For now the only option. If more variable file types are added then this needs to be changed to a switch case
                parse = false;
            }
        }
        if(parse) {
            // Check if study has a different variable file already using variablefile reference and fileId
            // Get latest revision, doesn't matter if it's a draft or not since file reference should be immutable
            // We can assume that we get a revision since other points before this depend on the existence of the revision
            RevisionEntity revEntity = em.find(RevisionEntity.class, study.latestRevisionKey());
            RevisionData data = json.readRevisionDataFromString(revEntity.getData());
            SavedDataField field = data.dataField(SavedDataFieldCall.get("variablefile")).getRight();
            if(field != null && field.hasValue()) {
                if(!field.getActualValue().equals(fileId.toString())) {
                    parse = false;
                }
            }
        }
        if(parse) {
            // Check if queue already contains a file with different path marked for variable parsing
            List<StudyAttachmentQueueEntity> fileLinks =
                    em.createQuery(
                        "SELECT l FROM StudyAttachmentQueueEntity l " +
                        "WHERE l.targetStudy=:study AND l.studyAttachmentId <> :attachment AND l.type IS NOT NULL",
                            StudyAttachmentQueueEntity.class)
                    .setParameter("study", studyId)
                    .setParameter("attachment", fileId)
                    .getResultList();
            if(fileLinks.size() > 0) {
                parse = false;
            }
        }
        if(parse) {
            // Add a variable parser type based on file extension
            if(FilenameUtils.getExtension(path).toUpperCase().equals("POR")) {
                queue.setType(VariableDataType.POR);
            }
        }

        em.persist(queue);
    }
}
