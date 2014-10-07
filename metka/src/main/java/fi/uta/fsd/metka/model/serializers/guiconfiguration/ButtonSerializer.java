package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.guiconfiguration.Button;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;

public class ButtonSerializer extends ObjectSerializer<Button> {
    @Override
    public void doSerialize(Button value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        // Write type
        if(value.getType() != null) {
            jgen.writeStringField("type", value.getType().name());
        } else {
            jgen.writeNullField("type");
        }

        if(value.getCustomHandler() != null) {
            jgen.writeStringField("customHandler", value.getCustomHandler());
        } else {
            jgen.writeNullField("customHandler");
        }

        // Write &title
        if(value.getTitle() != null) {
            jgen.writeObjectField("&title", value.getTitle());
        } else {
            jgen.writeNullField("&title");
        }

        // Write isHandler
        if(value.getIsHandler() != null) {
            jgen.writeBooleanField("isHandler", value.getIsHandler());
        } else {
            jgen.writeNullField("isHandler");
        }

        // Write isHandler
        if(value.getHasHandler() != null) {
            jgen.writeBooleanField("hasHandler", value.getHasHandler());
        } else {
            jgen.writeNullField("hasHandler");
        }

        // Write userGroups
        jgen.writeArrayFieldStart("permissions");
        for(String group : value.getPermissions()) {
            jgen.writeString(group);
        }
        jgen.writeEndArray();

        // Write states
        jgen.writeArrayFieldStart("states");
        for(VisibilityState state : value.getStates()) {
            jgen.writeString(state.name());
        }
        jgen.writeEndArray();
    }
}
