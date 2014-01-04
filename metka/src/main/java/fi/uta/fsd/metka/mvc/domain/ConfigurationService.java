package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/4/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ConfigurationService {
    @Autowired
    private ConfigurationRepository repository;

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
}
