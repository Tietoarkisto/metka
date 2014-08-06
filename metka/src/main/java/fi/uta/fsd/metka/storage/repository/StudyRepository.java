package fi.uta.fsd.metka.storage.repository;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface StudyRepository {
    /**
     * If given id/revision matches a STUDY DRAFT:
     *   Perform checks against FileLinkQueue for any recently added files.
     *   Check that those references are found from RevisionData
     *   If file is a por file then parse file and merge into current RevisionData
     *
     * // TODO: Generalise this out from StudyRepository, there's no need to handle it here specifically.
     * @param id RevisionableId
     * @param revision RevisionNo
     */
    public void checkFileLinkQueue(Long id, Integer revision);

    // TODO: Some parts of this need to be done in the generalised revision approval process somehow
    public boolean approveStudy(Object id);

    // TODO: This is done through the generalized revision edit
    //public RevisionData editStudy(Object studyno);
}
