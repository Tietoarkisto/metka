package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.guiconfiguration.FieldDescription;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;

public class FieldDescriptionSerializer extends ObjectSerializer<FieldDescription> {
    @Override
    public void doSerialize(FieldDescription value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStringField("key", value.getKey());
        if(value.getDisplayType() == null) {
            jgen.writeNullField("displayType");
        } else {
            jgen.writeStringField("displayType", value.getDisplayType().name()); // This is ignored for now but it's included here for completeness
        }
        jgen.writeBooleanField("multiline", value.getMultiline());
        jgen.writeBooleanField("multichoice", value.getMultichoice()); // This is ignored for now but it's included here for completeness
        jgen.writeArrayFieldStart("columnFields");
        for(String column : value.getColumnFields()) {
            jgen.writeString(column);
        }
        jgen.writeEndArray();
        jgen.writeBooleanField("showSaveInfo", value.getShowSaveInfo());
        jgen.writeBooleanField("showReferenceValue", value.getShowReferenceValue());
        jgen.writeBooleanField("displayHeader", value.getDisplayHeader());
    }
}
