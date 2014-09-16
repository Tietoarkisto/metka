package fi.uta.fsd.metka.storage.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.change.Change;
import fi.uta.fsd.metka.model.data.change.ContainerChange;
import fi.uta.fsd.metka.model.data.change.RowChange;
import fi.uta.fsd.metka.model.data.container.*;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.interfaces.ModelBase;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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

    public Pair<SerializationResults, Configuration> deserializeDataConfiguration(File file) {
        return deserializeFromFile(file, Configuration.class);
    }
    public Pair<SerializationResults, Configuration> deserializeDataConfiguration(String data) {
        return deserializeFromString(data, Configuration.class);
    }

    public Pair<SerializationResults, GUIConfiguration> deserializeGUIConfiguration(File file) {
        return deserializeFromFile(file, GUIConfiguration.class);
    }
    public Pair<SerializationResults, GUIConfiguration> deserializeGUIConfiguration(String data) {
        return deserializeFromString(data, GUIConfiguration.class);
    }

    public Pair<SerializationResults, RevisionData> deserializeRevisionData(String data) {
        return deserializeFromString(data, RevisionData.class);
    }

    public Pair<SerializationResults, TransferData> deserializeTransferData(String data) {
        return deserializeFromString(data, TransferData.class);
    }

    private <T extends ModelBase> Pair<SerializationResults, T> deserializeFromString(String data, Class<T> tClass) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(data, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from string data");
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    private <T extends ModelBase> Pair<SerializationResults, T> deserializeFromFile(File file, Class<T> tClass) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readValue(file, tClass));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while parsing "+tClass.toString()+" from file "+file.getName());
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, String> serialize(ModelBase data) {
        if(data instanceof RevisionData) {
            loopThroughChanges(((RevisionData) data).getChanges(), "");
            loopThroughFields(((RevisionData)data).getFields(), "");
        }
        try {
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing "+data.toString());
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_FAILED, null);
        }
    }

    private void loopThroughChanges(Map<String, Change> changes, String root) {
        if(StringUtils.hasText(root)) root+=".";
        for(String key : changes.keySet()) {
            if(key == null) {
                System.err.println("Null key in "+root);
            } else {
                Change change = changes.get(key);
                if(change instanceof ContainerChange) {
                    String changeRoot = root + change.getKey();

                    for(Integer i : ((ContainerChange)change).getRows().keySet()) {
                        if(i == null) {
                            System.err.println("Null rowid in "+changeRoot);
                        } else {
                            RowChange rowChange = ((ContainerChange)change).getRows().get(i);
                            loopThroughChanges(rowChange.getChanges(), changeRoot);
                        }
                    }
                }
            }
        }
    }

    private void loopThroughFields(Map<String, DataField> fields, String root) {
        if(StringUtils.hasText(root)) root += ".";
        for(String key : fields.keySet()) {
            if(key == null) {
                System.err.println("Null key in fields "+root);
            } else {
                DataField field = fields.get(key);
                if(field instanceof ContainerDataField) {
                    checkContainerDataField((ContainerDataField)field, root);
                } else if(field instanceof ValueDataField) {
                    checkValueDataField((ValueDataField) field, root);
                }
            }
        }
    }

    private void checkContainerDataField(ContainerDataField field, String root) {
        root += field.getKey();

        for(Language language : field.getRows().keySet()) {
            if(language == null) {
                System.err.println("Null language in "+root);
            } else {
                String rowLangRoot = root+"."+language;
                for(DataRow row : field.getRowsFor(language)) {
                    loopThroughFields(row.getFields(), rowLangRoot+"."+row.getRowId());
                }
            }
        }
    }

    private void checkValueDataField(ValueDataField field, String root) {
        root += field.getKey();
        String tempPath = root + ".original.";
        checkValueDataFieldMap(field.getOriginal(), tempPath);
        tempPath = root + ".current.";
        checkValueDataFieldMap(field.getOriginal(), tempPath);
    }

    private void checkValueDataFieldMap(Map<Language, ValueContainer> values, String root) {
        for(Language language : values.keySet()) {
            if(language == null) {
                System.err.println("Null language in "+root);
            }
        }
    }

    public Pair<SerializationResults, String> serialize(JsonNode data) {
        try {
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_SUCCESS, metkaObjectMapper.writeValueAsString(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while serializing JsonNode");
            return new ImmutablePair<>(SerializationResults.SERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, JsonNode> deserializeToJsonTree(File file) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(file));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading file "+file.getName());
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }

    public Pair<SerializationResults, JsonNode> deserializeToJsonTree(String data) {
        try {
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_SUCCESS, metkaObjectMapper.readTree(data));
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while reading String");
            return new ImmutablePair<>(SerializationResults.DESERIALIZATION_FAILED, null);
        }
    }
}
