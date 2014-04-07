package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Field;

import java.io.IOException;

/**
 * Serializes Configuration Field including only information pertaining to the field type.
 */
public class FieldSerializer extends JsonSerializer<Field> {
    @Override
    @SuppressWarnings("fallthrough")
    public void serialize(Field value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("type", value.getType().toString());
        jgen.writeBooleanField("translatable", value.getTranslatable());
        jgen.writeArrayFieldStart("notToLang");
        for(String str : value.getNotToLang()) {
            jgen.writeString(str);
        }
        jgen.writeEndArray();
        jgen.writeBooleanField("immutable", value.getImmutable());
        jgen.writeBooleanField("display", value.getDisplay());
        jgen.writeBooleanField("unique", value.getUnique());
        jgen.writeBooleanField("required", value.getRequired()); // TODO: Required is going to be more complicated later
        if(value.getSection() == null) {
            jgen.writeNullField("section");
        } else {
            jgen.writeStringField("section", value.getSection());
        }
        jgen.writeBooleanField("subfield", value.getSubfield());
        if(value.getSubfield()) {
            jgen.writeBooleanField("summaryField", value.getSummaryField());
            // TODO: Handle field referenceKey
        }
        jgen.writeBooleanField("editable", value.getEditable());

        switch(value.getType()) {
            case REFERENCECONTAINER:
                jgen.writeBooleanField("showReferenceKey", value.getShowReferenceKey());
                jgen.writeStringField("reference", value.getReference());
                /* FALLTHROUGH */
            case CONTAINER:
                if(value.getMaxValues() == null) {
                    jgen.writeNullField("maxValues");
                } else {
                    jgen.writeNumberField("maxValues", value.getMaxValues());
                }
                jgen.writeBooleanField("showSaveInfo", value.getShowSaveInfo());
                jgen.writeArrayFieldStart("subfields");
                for(String str : value.getSubfields()) {
                    jgen.writeString(str);
                }
                jgen.writeEndArray();
                break;
            case CHOICE:
                jgen.writeStringField("choicelist", value.getChoicelist());
                break;
            case REFERENCE:
                jgen.writeStringField("reference", value.getReference());
                break;
            case STRING:
                jgen.writeBooleanField("multiline", value.getMultiline());
                break;
            case CONCAT:
                jgen.writeArrayFieldStart("concatenate");
                for(String str : value.getConcatenate()) {
                    jgen.writeString(str);
                }
                jgen.writeEndArray();
                break;
            default:
                // Nothing to handle
                break;
        }

        jgen.writeEndObject();
    }
}
