package fi.uta.fsd.metka.transfer.revision;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = RevisionSaveResponse.RevisionSaveResponseSerializer.class)
public class RevisionSaveResponse {
    private final Map<String, ErrorMessage> configurations = new HashMap<>();

    public void setConfiguration(ErrorMessage message) {
        //configurations.put(message.getMsg().getKey().getType().toValue(), message);
    }

    public static class RevisionSaveResponseSerializer extends JsonSerializer<RevisionSaveResponse> {
        @Override
        public void serialize(RevisionSaveResponse value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            /*jgen.writeStartObject();
            jgen.writeBooleanField('success');
            jgen.writeObjectFieldStart('status');*/

//            for(Map.Entry<String, Configuration> entry : value.getConfigurations().entrySet()) {
//                jgen.writeObjectField(entry.getValue().getKey().getType().toValue(), entry.getValue());
//            }

            jgen.writeEndObject();
        }
    }
}