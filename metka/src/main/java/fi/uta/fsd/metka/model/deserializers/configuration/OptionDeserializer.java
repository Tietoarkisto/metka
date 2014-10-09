package fi.uta.fsd.metka.model.deserializers.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.io.IOException;

public class OptionDeserializer extends ObjectDeserializer<Option> {

    @Override
    protected Option doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode value = node.get("value");
        if(value == null || value.getNodeType() == JsonNodeType.NULL) {
            return null;
        }

        Option o = new Option(value.textValue());

        // Set deprecated
        JsonNode dep = node.get("deprecated");
        if(dep != null && dep.getNodeType() != JsonNodeType.NULL) {
            o.setDeprecated(dep.asBoolean());
        }

        // Set title
        JsonNode title = node.get("title");
        if(title == null) {
            title = node.get("&title");
        }

        TranslationObject loc = null;
        if(title != null) {
            loc = oc.treeToValue(title, TranslationObject.class);
        }
        o.setTitle(loc);

        return o;
    }
}
