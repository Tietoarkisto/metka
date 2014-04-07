package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Choicelist;
import fi.uta.fsd.metka.model.configuration.Option;

import java.io.IOException;

/**
 * Serializer for Configuration Choicelist pertaining information only to choicelist type
 */
public class ChoicelistSerializer extends JsonSerializer<Choicelist> {

    @Override
    public void serialize(Choicelist value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("default", value.getDef());
        jgen.writeStringField("type", value.getType().toString());
        jgen.writeBooleanField("includeEmpty", value.getIncludeEmpty());
        switch(value.getType()) {
            case VALUE:
            case LITERAL:
                jgen.writeArrayFieldStart("options");
                for(Option option : value.getOptions()) {
                    jgen.writeObject(option);
                }
                jgen.writeEndArray();
                break;
            case REFERENCE:
                jgen.writeStringField("reference", value.getReference());
                break;
            case SUBLIST:
                // Sublist has no special attributes
                break;
        }

        jgen.writeEndObject();
    }
}
