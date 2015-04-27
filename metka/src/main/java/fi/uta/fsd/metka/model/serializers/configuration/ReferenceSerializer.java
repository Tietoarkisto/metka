package fi.uta.fsd.metka.model.serializers.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;

/**
 * Serializes Configuration Reference including only information pertaining to the reference type.
 */
public class ReferenceSerializer extends ObjectSerializer<Reference> {
    @Override
    public void doSerialize(Reference value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("type", value.getType().toString());
        if(value.getTitlePath() == null) {
            jgen.writeNullField("titlePath");
        } else {
            jgen.writeStringField("titlePath", value.getTitlePath());
        }
        switch(value.getType()) {
            case REVISION: {
                ConfigurationType confType = ConfigurationType.fromValue(value.getTarget());
                jgen.writeStringField("target", confType.toValue());
                break;
            }
            case REVISIONABLE: {
                ConfigurationType confType = ConfigurationType.fromValue(value.getTarget());
                jgen.writeStringField("target", confType.toValue());
                jgen.writeBooleanField("approvedOnly", value.getApprovedOnly());
                jgen.writeBooleanField("ignoreRemoved", value.getIgnoreRemoved());
                break;
            }
            case JSON:
            case DEPENDENCY: {
                jgen.writeStringField("target", value.getTarget());
                jgen.writeStringField("valuePath", value.getValuePath());
                break;
            }
        }

    }
}
