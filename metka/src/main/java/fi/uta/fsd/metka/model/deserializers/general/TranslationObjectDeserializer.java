package fi.uta.fsd.metka.model.deserializers.general;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;

public class TranslationObjectDeserializer extends ObjectDeserializer<TranslationObject> {
    @Override
    public TranslationObject doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        TranslationObject loc = new TranslationObject();
        if(node.getNodeType() == JsonNodeType.STRING && StringUtils.hasText(node.textValue())) {
            loc.getTexts().put("default", node.textValue());
            return loc;
        } else if(node.getNodeType() == JsonNodeType.OBJECT) {
            JsonNode def = node.get("default");
            if(def != null && StringUtils.hasText(def.textValue())) {
                Iterator<String> iter = (node).fieldNames();
                while(iter.hasNext()) {
                    String lang = iter.next();
                    JsonNode text = node.get(lang);
                    if(text.getNodeType() == JsonNodeType.STRING) {
                        loc.getTexts().put(lang, text.textValue());
                    } else {
                        loc.getTexts().put(lang, "");
                    }
                }
                return loc;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
