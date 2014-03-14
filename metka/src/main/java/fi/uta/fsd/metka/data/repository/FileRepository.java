package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional(readOnly = true)
public interface FileRepository {
    @Transactional(readOnly = false) public RevisionData newFileRevisionable(String path) throws IOException;
}
