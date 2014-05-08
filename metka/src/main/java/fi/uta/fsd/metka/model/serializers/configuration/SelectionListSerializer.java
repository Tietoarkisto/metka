package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Serializer for Configuration SelectionList pertaining information only to selection list type
 */
public class SelectionListSerializer extends JsonSerializer<SelectionList> {

    @Override
    public void serialize(SelectionList value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("default", value.getDef());
        jgen.writeStringField("type", value.getType().toString());
        jgen.writeBooleanField("includeEmpty", value.getIncludeEmpty());
        jgen.writeArrayFieldStart("freeText");
        for(String free : value.getFreeText()) {
            jgen.writeString(free);
        }
        jgen.writeEndArray();
        if(!StringUtils.isEmpty(value.getFreeTextKey())) {
            jgen.writeStringField("freeTextKey", value.getFreeTextKey());
        }
        switch(value.getType()) {
            case VALUE:
            case LITERAL:
                jgen.writeArrayFieldStart("options");
                for(Option option : value.getOptions()) {
                    jgen.writeObject(option);
                }
                jgen.writeEndArray();
                break;
            case REFERENCE:
                jgen.writeStringField("reference", value.getReference());
                break;
            case SUBLIST:
                // Sublist has no special attributes
                break;
        }

        jgen.writeEndObject();
    }
}
