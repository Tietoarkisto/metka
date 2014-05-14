package fi.uta.fsd.metka.transfer.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.uta.fsd.metka.model.ModelBase;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for multiple configurations.
 * Can be serialized using JSONUtil
 */
@JsonSerialize(using = GUIConfigurationMap.GUIConfigurationMapSerializer.class)
public class GUIConfigurationMap implements ModelBase {
    private final Map<String, GUIConfiguration> configurations = new HashMap<>();

    public void setConfiguration(GUIConfiguration config) {
        configurations.put(config.getKey().getType().toValue(), config);
    }

    public Map<String, GUIConfiguration> getConfigurations() {
        return configurations;
    }

    public static class GUIConfigurationMapSerializer extends JsonSerializer<GUIConfigurationMap> {
        @Override
        public void serialize(GUIConfigurationMap value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();

            for(Map.Entry<String, GUIConfiguration> entry : value.getConfigurations().entrySet()) {
                jgen.writeObjectField(entry.getValue().getKey().getType().toValue(), entry.getValue());
            }

            jgen.writeEndObject();
        }
    }
}
