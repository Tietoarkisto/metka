package fi.uta.fsd.metka.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public abstract class ObjectSerializer<T> extends JsonSerializer<T> {
    @Override
    public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        doSerialize(value, jgen, provider);

        jgen.writeEndObject();
    }

    protected abstract void doSerialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException;
}
