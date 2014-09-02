package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;

/**
 * Serializes Configuration Field including only information pertaining to the field type.
 */
public class FieldSerializer extends ObjectSerializer<Field> {
    @Override
    @SuppressWarnings("fallthrough")
    public void doSerialize(Field value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("type", value.getType().toString());
        jgen.writeBooleanField("translatable", value.getTranslatable());
        jgen.writeBooleanField("immutable", value.getImmutable());
        jgen.writeBooleanField("display", value.getDisplay());
        jgen.writeBooleanField("unique", value.getUnique());
        jgen.writeBooleanField("required", value.getRequired()); // TODO: Required is going to be more complicated later
        jgen.writeBooleanField("subfield", value.getSubfield());
        jgen.writeBooleanField("editable", value.getEditable());
        jgen.writeBooleanField("writable", value.getWritable());
        jgen.writeBooleanField("indexed", value.getIndexed());

        if(!value.getType().isContainer()) {
            jgen.writeBooleanField("exact", value.getExact());
        }

        switch(value.getType()) {
            case REFERENCECONTAINER:
                jgen.writeStringField("reference", value.getReference());
                /* FALLTHROUGH */
            case CONTAINER:
                if(value.getMaxValues() == null) {
                    jgen.writeNullField("maxValues");
                } else {
                    jgen.writeNumberField("maxValues", value.getMaxValues());
                }
                jgen.writeArrayFieldStart("subfields");
                for(String str : value.getSubfields()) {
                    jgen.writeString(str);
                }
                jgen.writeEndArray();
                break;
            case SELECTION:
                jgen.writeStringField("selectionList", value.getSelectionList());
                break;
            case REFERENCE:
                jgen.writeStringField("reference", value.getReference());
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

    }
}
