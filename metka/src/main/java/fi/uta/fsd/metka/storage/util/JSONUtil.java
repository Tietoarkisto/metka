package fi.uta.fsd.metka.storage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

    public Pair<ReturnResult, Configuration> deserializeDataConfiguration(File file) {
        return deserializeFromFile(file, Configuration.class);
    }
    public Pair<ReturnResult, Configuration> deserializeDataConfiguration(String data) {
        return deserializeFromString(data, Configuration.class);
    }

    public Pair<ReturnResult, GUIConfiguration> deserializeGUIConfiguration(File file) {
        return deserializeFromFile(file, GUIConfiguration.class);
    }
    public Pair<ReturnResult, GUIConfiguration> deserializeGUIConfiguration(String data) {
        return deserializeFromString(data, GUIConfiguration.class);
    }

    public Pair<ReturnResult, RevisionData> deserializeRevisionData(String data) {
        return deserializeFromString(data, RevisionData.class);
    }

    public Pair<ReturnResult, TransferData> deserializeTransferData(String data) {
        return deserializeFromString(data, TransferData.class);
    }

    private <T extends ModelBase> Pair<ReturnResult, T> deserializeFromString(String data, Class<T> tClass) {
        try {
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(data, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from string data");
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_FAILED, null);
        }
    }

    private <T extends ModelBase> Pair<ReturnResult, T> deserializeFromFile(File file, Class<T> tClass) {
        try {
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(file, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from file "+file.getName());
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<ReturnResult, String> serialize(ModelBase data) {
        try {
            return new ImmutablePair<>(ReturnResult.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing "+data.toString());
            return new ImmutablePair<>(ReturnResult.SERIALIZATION_FAILED, null);
        }
    }

    public Pair<ReturnResult, String> serialize(JsonNode data) {
        try {
            return new ImmutablePair<>(ReturnResult.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing JsonNode");
            return new ImmutablePair<>(ReturnResult.SERIALIZATION_FAILED, null);
        }
    }

    public Pair<ReturnResult, JsonNode> deserializeToJsonTree(File file) {
        try {
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(file));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading file "+file.getName());
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<ReturnResult, JsonNode> deserializeToJsonTree(String data) {
        try {
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading String");
            return new ImmutablePair<>(ReturnResult.DESERIALIZATION_FAILED, null);
        }
    }
}
