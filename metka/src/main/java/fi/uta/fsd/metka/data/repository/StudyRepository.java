package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.simple.TransferObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
public interface StudyRepository {
    public RevisionData getNew(Integer acquisition_number) throws IOException;
    public boolean saveStudy(TransferObject so) throws IOException;
}
