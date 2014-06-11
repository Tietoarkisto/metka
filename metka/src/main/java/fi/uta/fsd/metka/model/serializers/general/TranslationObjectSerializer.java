package fi.uta.fsd.metka.model.serializers.general;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;
import java.util.Map;

public class TranslationObjectSerializer extends ObjectSerializer<TranslationObject> {
    @Override
    public void doSerialize(TranslationObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        for(Map.Entry<String, String> text : value.getTexts().entrySet()) {
            jgen.writeStringField(text.getKey(), text.getValue());
        }
    }
}
