package fi.uta.fsd.metka.model.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.IOException;

public abstract class ObjectDeserializer<T> extends JsonDeserializer<T> {
    protected ObjectCodec oc;
    protected JsonNode node;

    @Override
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        oc = jp.getCodec();
        node = oc.readTree(jp);

        if(node == null || node.getNodeType() == JsonNodeType.NULL) {
            return null;
        }

        return doDeserialize(jp, ctxt);
    }

    protected abstract T doDeserialize(JsonParser jp, DeserializationContext ctxt) throws IOException;
}
