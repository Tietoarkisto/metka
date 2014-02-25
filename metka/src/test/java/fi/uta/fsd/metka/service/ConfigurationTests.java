package fi.uta.fsd.metka.service;

import fi.uta.fsd.metka.MetkaTestModel;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.model.configuration.Field;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ConfigurationTests extends MetkaTestModel {
    @Autowired
    private ConfigurationRepository repo;

    @Test
    public void confTest() throws Exception {

        Configuration conf = repo.findConfiguration(new ConfigurationKey(ConfigurationType.SERIES, 1));
        Field field = conf.getFields().get("id");
        assertNotNull(conf);
        assertNotNull(field);
    }
}
