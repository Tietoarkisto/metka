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

    public void insertDataConfig(String text) {
        configurationService.insertDataConfig(text);
    }

    public void insertGUIConfig(String text) {
        configurationService.insertGUIConfig(text);
    }

    public void insertMisc(String text) {
        try {
            miscJSONRepository.insert(text);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
