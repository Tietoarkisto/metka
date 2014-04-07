package fi.uta.fsd.metka.transfer.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.uta.fsd.metka.model.ModelBase;
import fi.uta.fsd.metka.model.configuration.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for multiple configurations.
 * Can be serialized using JSONUtil
 */
@JsonSerialize(using = ConfigurationMap.ConfigurationMapSerializer.class)
public class ConfigurationMap implements ModelBase {
    private final Map<String, Configuration> configurations = new HashMap<>();

    public void setConfiguration(Configuration config) {
        configurations.put(config.getKey().getType().toValue(), config);
    }

    public Map<String, Configuration> getConfigurations() {
        return configurations;
    }

    public static class ConfigurationMapSerializer extends JsonSerializer<ConfigurationMap> {
        @Override
        public void serialize(ConfigurationMap value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartObject();

            for(Map.Entry<String, Configuration> entry : value.getConfigurations().entrySet()) {
                jgen.writeObjectField(entry.getValue().getKey().getType().toValue(), entry.getValue());
            }

            jgen.writeEndObject();
        }
    }
}
