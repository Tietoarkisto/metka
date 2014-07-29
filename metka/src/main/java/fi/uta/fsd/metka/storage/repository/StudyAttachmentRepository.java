package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface StudyAttachmentRepository {
    public RevisionData studyAttachmentForPath(String path, Long studyId);

    /**
     * Creates a new study attachment with initial revision in DRAFT state
     *
     * @param studyId Id of the study this study attachment is attached to.
     * @returnś
     */
    public RevisionData newStudyAttachment(Long studyId);

    /**
     * Return a DRAFT revision for given STUDY_ATTACHMENT.
     * If there's no existing DRAFT then one is created, otherwise returns an existing draft
     * @param id Revisionable id
     * @return RevisionData for DRAFT revision for given file, if none exists one is created.
     */
    public RevisionData getEditableStudyAttachmentRevision(Long id);

    /**
     * Adds a row to FILE_LINK_QUEUE for future checking that a reference actually exists in target revisionable.
     * Will also make a note if the file is a por file in need of parsing and adding to a STUDY.
     * It is assumed that this is handled before a DRAFT is approved so there's only need to consider current
     * latest revision (that should be a draft)
     * @param studyId RevisionableId from where the file should be found.
     * @param attachmentId RevisionableId of the File that should be linked
     * @param key Field key of the REFERENCECONTAINER where the reference should be found
     * @param path File path, used to detect a por file
     */
    public void addFileLinkEvent(Long studyId, Long attachmentId, String key, String path);
}
