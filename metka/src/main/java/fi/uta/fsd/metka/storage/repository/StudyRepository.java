package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface StudyRepository {
    public RevisionData getNew(Long acquisition_number);
    public boolean saveStudy(TransferObject so);

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

    public boolean approveStudy(Object id);
    public RevisionData editStudy(Object studyno);
}
