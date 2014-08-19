package fi.uta.fsd.metka.model.deserializers.general;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;

public class TranslationObjectDeserializer extends ObjectDeserializer<TranslationObject> {
    @Override
    public TranslationObject doDeserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TranslationObject loc = new TranslationObject();
        if(node.getNodeType() == JsonNodeType.STRING && StringUtils.hasText(node.textValue())) {
            loc.getTexts().put("default", node.textValue());
            return loc;
        } else if(node.getNodeType() == JsonNodeType.OBJECT) {
            JsonNode def = node.get("default");
            if(def != null && StringUtils.hasText(def.textValue())) {
                Iterator<String> iter = ((ObjectNode)node).fieldNames();
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
    }/*

    public static TranslationObject deserialize(JsonNode node, String name) {
        JsonNode text = node.get(name);
        if(text == null) {
            text = node.get("&"+name);
        }
        if(text == null || text.getNodeType() == JsonNodeType.NULL) {
            return null;
        }

        TranslationObject loc = new TranslationObject();
        if(text.getNodeType() == JsonNodeType.STRING && StringUtils.hasText(text.textValue())) {
            loc.getTexts().put("default", text.textValue());
            return loc;
        } else if(text.getNodeType() == JsonNodeType.OBJECT) {
            JsonNode def = text.get("default");
            if(def != null && StringUtils.hasText(def.textValue())) {
                Iterator<String> iter = ((ObjectNode)text).fieldNames();
                while(iter.hasNext()) {
                    String lang = iter.next();
                    JsonNode trans = text.get(lang);
                    if(trans.getNodeType() == JsonNodeType.STRING) {
                        loc.getTexts().put(lang, trans.textValue());
                    } else {
                        loc.getTexts().put(lang, "");
                    }
                }
                return loc;
            } else {
                return null;
            }
        }
        return null;
    }*/
}
