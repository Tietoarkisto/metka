package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.data.enums.ButtonType;
import fi.uta.fsd.metka.data.enums.VisibilityState;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.Button;

import java.io.IOException;

public class ButtonDeserializer extends JsonDeserializer<Button> {

    @Override
    public Button deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        Button btn = new Button();

        // Set title
        JsonNode title = node.get("title");
        if(title == null) {
            title = node.get("&title");
        }

        TranslationObject loc = null;
        if(title != null) {
            loc = oc.treeToValue(title, TranslationObject.class);
        }
        btn.setTitle(loc);

        // Set handler
        JsonNode handler = node.get("isHandler");
        if(handler != null && handler.getNodeType() == JsonNodeType.BOOLEAN) {
            btn.setHandler(handler.booleanValue());
        }

        // Set user groups
        JsonNode groups = node.get("userGroups");
        if(groups != null && groups.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode group : groups) {
                if(group.getNodeType() == JsonNodeType.STRING) {
                    btn.getUserGroups().add(group.textValue());
                }
            }
        }

        // Set type
        JsonNode type = node.get("type");
        if(type != null && type.getNodeType() == JsonNodeType.STRING) {
            btn.setType(ButtonType.valueOf(type.textValue()));
        }

        // Set states
        JsonNode states = node.get("states");
        if(states != null && states.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode state : states) {
                if(state.getNodeType() == JsonNodeType.STRING) {
                    btn.getStates().add(VisibilityState.valueOf(state.textValue()));
                }
            }
        }

        return btn;
    }
}
