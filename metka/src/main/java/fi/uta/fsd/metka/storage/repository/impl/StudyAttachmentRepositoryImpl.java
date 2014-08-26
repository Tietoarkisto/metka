package fi.uta.fsd.metka.storage.repository.impl;

import fi.uta.fsd.metka.model.factories.StudyAttachmentFactory;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class StudyAttachmentRepositoryImpl {
    private static Logger logger = LoggerFactory.getLogger(StudyAttachmentRepositoryImpl.class);
    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private StudyAttachmentFactory factory;

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
    /*public RevisionData studyAttachmentForPath(String path, Long studyId) {
        // Get all study attachments with requested path
        List<StudyAttachmentEntity> attachments = em.
                createQuery("SELECT r FROM StudyAttachmentEntity r WHERE r.removed = false AND r.filePath=:path", StudyAttachmentEntity.class)
                .setParameter("path", path)
                .getResultList();

        // Go through attachments checking their latest revision
        // If there is a 'file' field and if the value in that field matches given path then break
        RevisionData revision = null;
        for(StudyAttachmentEntity attachment : attachments) {
            Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(attachment.latestRevisionKey(), ConfigurationType.STUDY_ATTACHMENT);
            if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
                continue;
            }
            Pair<StatusCode, ValueDataField> fieldPair = pair.getRight().dataField(ValueDataFieldCall.get("file"));
            if(fieldPair.getLeft() != StatusCode.FIELD_FOUND) {
                continue;
            }
            ValueDataField field = fieldPair.getRight();
            if(field.valueEquals(path)) {
                // Path match found
                revision = pair.getRight();
                break;
            } else if(field.hasValue()) {
                // Update path to latest
                attachment.setFilePath(field.getActualValue());
            }
        }

        // No previous study attachment with path found
        // Create new attachment with initial revision
        if(revision == null) {
            StudyAttachmentEntity entity = new StudyAttachmentEntity();
            em.persist(entity);

            // TODO: Use revision create repository
            RevisionEntity revisionEntity = new RevisionEntity(new RevisionKey(entity.getId(), 1));

            *//*
             * creates initial data set for the first draft any exceptions thrown should force rollback
             * automatically.
             * This assumes the entity has empty data field and is a draft.
            *//*
            //revision = factory.newData(revisionEntity, studyId);
            if(revision != null) {
                revision.dataField(ValueDataFieldCall.set("path").setValue(path));
                entity.setFilePath(path);
                em.persist(revisionEntity);
                entity.setLatestRevisionNo(revisionEntity.getKey().getRevisionNo());
            }
        } else {
            // Check that this revision belongs to given study
            ValueDataField studyField = revision.dataField(ValueDataFieldCall.get("study")).getRight();
            if(studyField == null || !studyField.hasValue() || !studyField.getActualValue().equals(studyId.toString())) {
                // TODO: Attachment exists but it's marked for some other study, log exception, we can't continue
                return null;
            }
        }

        return revision;
    }*/

    /*public RevisionData getEditableStudyAttachmentRevision(Long id) {
        StudyAttachmentEntity file = em.find(StudyAttachmentEntity.class, id);
        if(file == null) {
            logger.warn("Couldn't find study attachment with id "+id);
            return null;
        }
        Pair<ReturnResult, RevisionData> pair = general.getRevisionDataOfType(file.latestRevisionKey(), ConfigurationType.STUDY_ATTACHMENT);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("No revision was returned for "+file.latestRevisionKey()+" with result: "+pair.getLeft());
            return null;
        }

        RevisionData data = pair.getRight();
        // If DRAFT exists, return it
        if(data.getState() == RevisionState.DRAFT) {
            return data;
        } else {
            // Get latest configuration for configuration key for new data
            Pair<ReturnResult, Configuration> configPair = configRepo.findLatestConfiguration(ConfigurationType.STUDY_ATTACHMENT);
            if(configPair.getKey() != ReturnResult.CONFIGURATION_FOUND) {
                logger.error("Couldn't find configuration for STUDY_ATTACHMENT");
                return null;
            }
            // TODO: Use revision edit repository
            RevisionEntity newRevision = new RevisionEntity(new RevisionKey(file.getId(), file.getLatestRevisionNo()+1));


            // Create new RevisionData object using the current revEntity (either new or old, doesn't matter)
            //RevisionData newData = DataFactory.createNewRevisionData(newRevision, data, configPair.getRight().getKey());

            // TODO: Set handler

            *//*Pair<ReturnResult, String> string = json.serialize(newData);
            if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
                logger.error("Couldn't serialize "+newData.toString());
                return null;
            }

            newRevision.setData(string.getRight());
            em.persist(newRevision);
            file.setLatestRevisionNo(newRevision.getKey().getRevisionNo());
            return newData;*//*
            return null;
        }
    }*/

    /*public void addFileLinkEvent(Long studyId, Long fileId, String key, String path) {

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
            Pair<ReturnResult, RevisionData> pair = json.deserializeRevisionData(revEntity.getData());
            if(pair.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Couldn't deserialize "+revEntity.toString());
                parse = false;
            } else {
                ValueDataField field = pair.getRight().dataField(ValueDataFieldCall.get("variablefile")).getRight();
                if(field != null && field.hasValue()) {
                    if(!field.getActualValue().equals(fileId.toString())) {
                        parse = false;
                    }
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
    }*/
}
