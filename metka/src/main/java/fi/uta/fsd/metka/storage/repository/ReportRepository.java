package fi.uta.fsd.metka.storage.repository;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ReportRepository {
    public String gatherGeneralReport();
}
