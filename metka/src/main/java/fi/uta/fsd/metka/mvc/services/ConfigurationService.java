package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {
    @Autowired
    private ConfigurationRepository repository;

    public void insertDataConfig(String text) {
        repository.insertDataConfig(text);
    }

    public void insertGUIConfig(String text) {
        repository.insertGUIConfig(text);
    }

    public Pair<ReturnResult, Configuration> findLatestByType(ConfigurationType type) {
        return repository.findLatestConfiguration(type);
    }

    public Pair<ReturnResult, GUIConfiguration> findLatestGUIByType(ConfigurationType type) {
        return repository.findLatestGUIConfiguration(type);
    }

    public Pair<ReturnResult, Configuration> findByTypeAndVersion(ConfigurationKey key) {
        return repository.findConfiguration(key);
    }

    public Pair<ReturnResult, Configuration> findLatestByRevisionableId(Long id) {
        return repository.findLatestByRevisionableId(id);
    }

    public Pair<ReturnResult, Configuration> findConfigurationForRevision(Long id, Integer revision) {
        return repository.findConfigurationForRevision(id, revision);
    }
}
