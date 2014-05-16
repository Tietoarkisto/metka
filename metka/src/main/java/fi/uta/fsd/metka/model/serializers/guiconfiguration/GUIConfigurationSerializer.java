package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.data.enums.ContainerType;
import fi.uta.fsd.metka.model.guiconfiguration.Button;
import fi.uta.fsd.metka.model.guiconfiguration.Container;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;

import java.io.IOException;

public class GUIConfigurationSerializer extends JsonSerializer<GUIConfiguration> {
    @Override
    public void serialize(GUIConfiguration value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();

        jgen.writeObjectField("key", value.getKey());

        jgen.writeArrayFieldStart("content");
        for(Container container : value.getContent()) {
            if(checkValidContainer(container.getType())) {
                jgen.writeObject(container);
            }
        }
        jgen.writeEndArray();

        jgen.writeArrayFieldStart("buttons");
        for(Button button : value.getButtons()) {
            jgen.writeObject(button);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();
    }

    private boolean checkValidContainer(ContainerType type) {
        // Cells and Rows are never part of content array on their own but instead always within a COLUMN container
        if(type == ContainerType.CELL) return false;
        if(type == ContainerType.ROW) return false;
        return true;
    }
}
