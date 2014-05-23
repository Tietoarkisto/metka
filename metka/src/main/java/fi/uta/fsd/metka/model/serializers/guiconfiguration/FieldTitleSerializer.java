package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.guiconfiguration.FieldTitle;

import java.io.IOException;

public class FieldTitleSerializer extends JsonSerializer<FieldTitle> {
    @Override
    public void serialize(FieldTitle value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeObjectField("&title", value.getTitle());

        jgen.writeEndObject();
    }
}
