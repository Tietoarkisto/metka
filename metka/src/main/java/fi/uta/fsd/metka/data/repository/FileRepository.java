package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.SavedReference;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional(readOnly = true)
public interface FileRepository {
    @Transactional(readOnly = false) public RevisionData newFileRevisionable(String path) throws IOException;

    public RevisionData findLatestRevision(Integer id) throws IOException;

    public SavedReference saveAndApprove(TransferObject to) throws Exception;
}
