package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.FieldTitle;

import java.io.IOException;

public class FieldTitleDeserializer extends JsonDeserializer<FieldTitle> {

    @Override
    public FieldTitle deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        FieldTitle ft = new FieldTitle();

        JsonNode key = node.get("key");
        ft.setKey(key.textValue());

        // Set title
        JsonNode title = node.get("title");
        if(title == null) {
            title = node.get("&title");
        }

        TranslationObject loc = null;
        if(title != null) {
            loc = oc.treeToValue(title, TranslationObject.class);
        }
        ft.setTitle(loc);

        return ft;
    }
}
