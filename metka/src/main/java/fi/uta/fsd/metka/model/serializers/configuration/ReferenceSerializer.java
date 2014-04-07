package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Reference;

import java.io.IOException;

/**
 * Serializes Configuration Reference including only information pertaining to the reference type.
 */
public class ReferenceSerializer extends JsonSerializer<Reference> {
    @Override
    public void serialize(Reference value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException, IllegalArgumentException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("type", value.getType().toString());
        switch(value.getType()) {
            case REVISIONABLE:
                ConfigurationType confType = ConfigurationType.fromValue(value.getTarget());
                jgen.writeStringField("target", confType.toValue());
                jgen.writeStringField("titlePath", value.getTitlePath());
                jgen.writeBooleanField("approvedOnly", value.getApprovedOnly());
                break;
            case JSON:
            case DEPENDENCY:
                jgen.writeStringField("target", value.getTarget());
                jgen.writeStringField("valuePath", value.getValuePath());
                if(value.getTitlePath() == null) {
                    jgen.writeNullField("titlePath");
                } else {
                    jgen.writeStringField("titlePath", value.getTitlePath());
                }
                break;
        }

        jgen.writeEndObject();
    }
}
