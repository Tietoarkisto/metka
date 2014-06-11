package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.guiconfiguration.FieldTitle;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;

public class FieldTitleSerializer extends ObjectSerializer<FieldTitle> {
    @Override
    public void doSerialize(FieldTitle value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStringField("key", value.getKey());
        if(value.getTitle() != null) {
            jgen.writeObjectField("&title", value.getTitle());
        } else {
            jgen.writeNullField("&title");
        }
    }
}
