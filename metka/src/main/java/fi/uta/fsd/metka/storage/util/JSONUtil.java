package fi.uta.fsd.metka.storage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Handles general JSON deserialization and serialization operations.
 */
@Service
public final class JSONUtil {
    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    // Private constructor to stop instantiation
    private JSONUtil() {}

    @Autowired
    private ObjectMapper metkaObjectMapper;

    public Configuration deserializeDataConfiguration(File file) {
        return deserializeFromFile(file, Configuration.class);
    }
    public Configuration deserializeDataConfiguration(String data) {
        return deserializeFromString(data, Configuration.class);
    }

    public GUIConfiguration deserializeGUIConfiguration(File file) {
        return deserializeFromFile(file, GUIConfiguration.class);
    }
    public GUIConfiguration deserializeGUIConfiguration(String data) {
        return deserializeFromString(data, GUIConfiguration.class);
    }

    public RevisionData deserializeRevisionData(String data) {
        return deserializeFromString(data, RevisionData.class);
    }

    private <T extends ModelBase> T deserializeFromString(String data, Class<T> tClass) {
        try {
            return metkaObjectMapper.readValue(data, tClass);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from string data");
            return null;
        }
    }

    private <T extends ModelBase> T deserializeFromFile(File file, Class<T> tClass) {
        try {
            return metkaObjectMapper.readValue(file, tClass);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from file "+file.getName());
            return null;
        }
    }

    public String serialize(ModelBase data) {
        try {
            return metkaObjectMapper.writeValueAsString(data);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing "+data.toString());
            return null;
        }
    }

    public String serialize(JsonNode data) {
        try {
            return metkaObjectMapper.writeValueAsString(data);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing JsonNode");
            return null;
        }
    }

    public JsonNode readJsonTree(File file) {
        try {
            return metkaObjectMapper.readTree(file);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading file "+file.getName());
            return null;
        }
    }

    public JsonNode readJsonTree(String data) {
        try {
            return metkaObjectMapper.readTree(data);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading String");
            return null;
        }
    }
}
