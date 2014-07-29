package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.RevisionState;
import fi.uta.fsd.metka.enums.VariableDataType;
import fi.uta.fsd.metka.model.access.calls.SavedDataFieldCall;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedDataField;
import fi.uta.fsd.metka.model.factories.DataFactory;
import fi.uta.fsd.metka.model.factories.FileFactory;
import fi.uta.fsd.metka.storage.entity.RevisionEntity;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.entity.StudyAttachmentQueueEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyAttachmentEntity;
import fi.uta.fsd.metka.storage.entity.impl.StudyEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.repository.StudyAttachmentRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
     */
    @Override
    public RevisionData studyAttachmentForPath(String path, Long studyId) {
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
            revision = json.deserializeRevisionData(revisionEntity.getData());
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
            revision = factory.newStudyAttachmentData(revisionEntity, studyId);
            revision.dataField(SavedDataFieldCall.set("path").setValue(path));
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

    /**
     * Creates new study attachment attachmed to given study
     *
     * @return RevisionData for a study attachment
     */
    @Override
    public RevisionData newStudyAttachment(Long studyId) {
        StudyAttachmentEntity entity = new StudyAttachmentEntity();
        entity.setStudyId(studyId);
        em.persist(entity);

        RevisionEntity revisionEntity = entity.createNextRevision();

        /*
         * creates initial data set for the first draft any exceptions thrown should force rollback
         * automatically.
         * This assumes the entity has empty data field and is a draft.
        */
        RevisionData revision = factory.newStudyAttachmentData(revisionEntity, studyId);
        em.persist(revisionEntity);

        entity.setLatestRevisionNo(revisionEntity.getKey().getRevisionNo());

        return revision;
    }

    @Override
    public RevisionData getEditableStudyAttachmentRevision(Long id) {
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
        RevisionData data = json.deserializeRevisionData(revision.getData());
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
    public void addFileLinkEvent(Long studyId, Long fileId, String key, String path) {
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
        if(StringUtils.isEmpty(path)) {
            parse = false;
        }
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
            RevisionData data = json.deserializeRevisionData(revEntity.getData());
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
