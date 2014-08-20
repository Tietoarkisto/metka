package fi.uta.fsd.metka.mvc.services;

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
}
