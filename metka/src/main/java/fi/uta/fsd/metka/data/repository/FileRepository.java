package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface FileRepository {
    public RevisionData newFileRevisionable(String path) throws IOException;

    /**
     * Return a DRAFT revision for given FILE.
     * If there's no existing DRAFT then one is created, otherwise returns an existing draft
     * @param id Revisionable id
     * @return RevisionData for DRAFT revision for given file, if none exists one is created.
     * @throws IOException
     */
    public RevisionData getEditableRevision(Integer id) throws IOException;

    /**
     * Takes given TransferObject and saves possible changes to FILE-object.
     * If no changes were found does nothing, but if there were changes saves the data and approves the FILE-object.
     * This will lead to new revision being created next time the file is opened.
     * @param to
     * @return
     * @throws Exception
     */
    public void saveAndApprove(TransferObject to) throws Exception;

    /**
     * Adds a row to FILE_LINK_QUEUE for future checking that a reference actually exists in target revisionable.
     * Will also make a note if the file is a por file in need of parsing and adding to a STUDY.
     * It is assumed that this is handled before a DRAFT is approved so there's only need to consider current
     * latest revision (that should be a draft)
     * @param targetId RevisionableId from where the file should be found.
     * @param fileId RevisionableId of the File that should be linked
     * @param key Field key of the REFERENCECONTAINER where the reference should be found
     * @param path File path, used to detect a por file
     */
    public void addFileLinkEvent(Integer targetId, Integer fileId, String key, String path);
}
