package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;


public class OptionSerializer extends ObjectSerializer<Option> {

    @Override
    protected void doSerialize(Option value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        // Write value
        if(StringUtils.hasText(value.getValue())) {
            jgen.writeStringField("value", value.getValue());
        } else {
            jgen.writeNullField("value");
        }

        // Write deprecated
        if(value.getDeprecated()) jgen.writeBooleanField("deprecated", value.getDeprecated());

        // Write &title
        if(value.getTitle() != null) {
            jgen.writeObjectField("&title", value.getTitle());
        } else {
            jgen.writeNullField("&title");
        }
    }
}
