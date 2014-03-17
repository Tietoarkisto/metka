package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


@Transactional(readOnly = true)
public interface ConfigurationRepository {
    @Transactional(readOnly = false) public void insert(Configuration configuration) throws IOException;
    public Configuration findConfiguration(ConfigurationKey key) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findLatestConfiguration(ConfigurationType type) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findLatestByRevisionableId(Integer id) throws IncorrectResultSizeDataAccessException, IOException;
}
