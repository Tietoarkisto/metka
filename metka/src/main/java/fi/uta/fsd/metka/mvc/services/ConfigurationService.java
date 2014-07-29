package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
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

    public Configuration findLatestByType(ConfigurationType type) {
        Configuration conf = null;
        try {
            conf = repository.findLatestConfiguration(type);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        return conf;
    }

    public GUIConfiguration findLatestGUIByType(ConfigurationType type) {
        GUIConfiguration conf = null;
        try {
            conf = repository.findLatestGUIConfiguration(type);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        return conf;
    }

    public Configuration findByTypeAndVersion(ConfigurationKey key) {
        Configuration conf = null;
        try {
            conf = repository.findConfiguration(key);
        } catch(Exception ex) {
            // TODO: better exception handling with messages to the user
            ex.printStackTrace();
            return null;
        }

        return conf;
    }

    public Configuration findLatestByRevisionableId(Long id) {
        Configuration conf = null;
        try {
            conf = repository.findLatestByRevisionableId(id);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return conf;
    }

    public Configuration findConfigurationForRevision(Long id, Integer revision) {
        Configuration conf = null;
        try {
            conf = repository.findConfigurationForRevision(id, revision);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return conf;
    }
}
