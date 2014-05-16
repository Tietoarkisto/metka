package fi.uta.fsd.metka.model.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.io.IOException;
import java.util.Map;

public class TranslationObjectSerializer extends JsonSerializer<TranslationObject> {
    @Override
    public void serialize(TranslationObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        for(Map.Entry<String, String> text : value.getTexts().entrySet()) {
            jgen.writeStringField(text.getKey(), text.getValue());
        }

        jgen.writeEndObject();
    }
}
