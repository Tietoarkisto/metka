package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface StudyRepository {
    public RevisionData getNew(Integer acquisition_number) throws IOException;
    public boolean saveStudy(TransferObject so) throws IOException;

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
    public void checkFileLinkQueue(Integer id, Integer revision) throws IOException;

    public boolean approveStudy(Object id) throws IOException;
    public RevisionData editStudy(Object studyno) throws IOException;
}
