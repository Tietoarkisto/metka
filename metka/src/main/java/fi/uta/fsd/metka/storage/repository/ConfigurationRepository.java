package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true)
public interface ConfigurationRepository {
    @Transactional(readOnly = false) public void insert(Configuration configuration);
    @Transactional(readOnly = false) public void insert(GUIConfiguration configuration);
    @Transactional(readOnly = false) public void insertDataConfig(String text);
    @Transactional(readOnly = false) public void insertGUIConfig(String text);
    public Configuration findConfiguration(String type, Integer version);
    public Configuration findConfiguration(ConfigurationType type, Integer version);
    public Configuration findConfiguration(ConfigurationKey key);
    public Configuration findLatestConfiguration(ConfigurationType type);
    public GUIConfiguration findLatestGUIConfiguration(ConfigurationType type);
    public Configuration findLatestByRevisionableId(Long id);
    public Configuration findConfigurationForRevision(Long id, Integer revision);
}
