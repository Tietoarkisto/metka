package fi.uta.fsd.metka.mvc.domain;

import fi.uta.fsd.metka.data.repository.MiscJSONRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SettingsService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    public void insertConfig(String text) {
        configurationService.insert(text);
    }

    public void insertMisc(String text) {
        try {
            miscJSONRepository.insert(text);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
