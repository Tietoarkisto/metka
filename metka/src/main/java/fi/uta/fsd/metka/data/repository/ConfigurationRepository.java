package fi.uta.fsd.metka.data.repository;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


@Transactional(readOnly = true)
public interface ConfigurationRepository {
    @Transactional(readOnly = false) public void insert(Configuration configuration) throws IOException;
    @Transactional(readOnly = false) public void insert(GUIConfiguration configuration) throws IOException;
    @Transactional(readOnly = false) public void insertDataConfig(String text) throws IOException;
    @Transactional(readOnly = false) public void insertGUIConfig(String text) throws IOException;
    public Configuration findConfiguration(String type, Integer version) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findConfiguration(ConfigurationType type, Integer version) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findConfiguration(ConfigurationKey key) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findLatestConfiguration(ConfigurationType type) throws IncorrectResultSizeDataAccessException, IOException;
    public GUIConfiguration findLatestGUIConfiguration(ConfigurationType type) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findLatestByRevisionableId(Long id) throws IncorrectResultSizeDataAccessException, IOException;
    public Configuration findConfigurationForRevision(Long id, Integer revision) throws IncorrectResultSizeDataAccessException, IOException;
}
