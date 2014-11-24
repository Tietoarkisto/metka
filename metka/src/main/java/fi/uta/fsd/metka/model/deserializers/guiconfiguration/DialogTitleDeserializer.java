package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.enums.DialogType;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.DialogTitle;

import java.io.IOException;
import java.util.Iterator;

public class DialogTitleDeserializer extends ObjectDeserializer<DialogTitle> {

    @Override
    public DialogTitle doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        DialogTitle dt = new DialogTitle();

        Iterator<String> iter = node.fieldNames();
        while(iter.hasNext()) {
            String name = iter.next();
            if(name.equals("key")) {
                dt.setKey(node.get(name).textValue());
            } else {
                JsonNode text = node.get(name);
                if(name.substring(0, 1).equals("&")) {
                    name = name.substring(1);
                }
                dt.getTypes().put(DialogType.valueOf(name), oc.treeToValue(text, TranslationObject.class));
            }
        }

        return dt;
    }
}