package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Serializer for Configuration SelectionList pertaining information only to selection list type
 */
public class SelectionListSerializer extends ObjectSerializer<SelectionList> {

    @Override
    public void doSerialize(SelectionList value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

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
                jgen.writeStringField("sublistKey", value.getSublistKey());
                break;
        }

    }
}
