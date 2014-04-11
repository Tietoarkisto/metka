package fi.uta.fsd.metka.data.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.model.ModelBase;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Handles general JSON deserialization and serialization operations.
 */
@Service
public class JSONUtil {
    @Autowired
    private ObjectMapper metkaObjectMapper;

    public Configuration readConfigurationFromFile(File file) throws IOException {
        return metkaObjectMapper.readValue(file, Configuration.class);
    }
    public Configuration readConfigurationFromString(String data) throws IOException {
        return metkaObjectMapper.readValue(data, Configuration.class);
    }

    public RevisionData readRevisionDataFromString(String data) throws IOException {
        return metkaObjectMapper.readValue(data, RevisionData.class);
    }

    public String serialize(ModelBase data) throws IOException {
        return metkaObjectMapper.writeValueAsString(data);
    }

    public String serialize(JsonNode data) throws IOException {
        return metkaObjectMapper.writeValueAsString(data);
    }

    public JsonNode readJsonTree(File file) throws IOException {
        return metkaObjectMapper.readTree(file);
    }

    public JsonNode readJsonTree(String data) throws IOException {
        return metkaObjectMapper.readTree(data);
    }
}
