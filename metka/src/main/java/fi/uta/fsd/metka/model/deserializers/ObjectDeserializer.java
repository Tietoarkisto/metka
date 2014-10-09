package fi.uta.fsd.metka.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;

public abstract class ObjectDeserializer<T> extends JsonDeserializer<T> {
    @Override
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        if(node == null || node.getNodeType() == JsonNodeType.NULL) {
            return null;
        }

        return doDeserialize(oc, node, jp, ctxt);
    }

    protected abstract T doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException;
}
