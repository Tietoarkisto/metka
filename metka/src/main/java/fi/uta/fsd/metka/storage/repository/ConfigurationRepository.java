package fi.uta.fsd.metka.storage.repository;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(readOnly = true)
public interface ConfigurationRepository {
    @Transactional(readOnly = false) public ReturnResult insert(Configuration configuration);
    @Transactional(readOnly = false) public ReturnResult insert(GUIConfiguration configuration);
    @Transactional(readOnly = false) public ReturnResult insertDataConfig(String text);
    @Transactional(readOnly = false) public ReturnResult insertGUIConfig(String text);
    public Pair<ReturnResult, Configuration> findConfiguration(String type, Integer version);
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationType type, Integer version);
    public Pair<ReturnResult, Configuration> findConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, Configuration> findLatestConfiguration(ConfigurationType type);
    public Pair<ReturnResult, Configuration> findLatestByRevisionableId(Long id);
    public Pair<ReturnResult, Configuration> findConfigurationForRevision(Long id, Integer revision);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(String type, Integer version);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationType type, Integer version);
    public Pair<ReturnResult, GUIConfiguration> findGUIConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, GUIConfiguration> findLatestGUIConfiguration(ConfigurationType type);

    public List<ConfigurationKey> getDataKeys();
    public List<ConfigurationKey> getGUIKeys();
    public Pair<ReturnResult, String> getDataConfiguration(ConfigurationKey key);
    public Pair<ReturnResult, String> getGUIConfiguration(ConfigurationKey key);
}
