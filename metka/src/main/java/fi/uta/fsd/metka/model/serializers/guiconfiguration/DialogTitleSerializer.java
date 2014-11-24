package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.enums.DialogType;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.DialogTitle;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;
import java.util.Map;

public class DialogTitleSerializer extends ObjectSerializer<DialogTitle> {
    @Override
    public void doSerialize(DialogTitle value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStringField("key", value.getKey());
        for(Map.Entry<DialogType, TranslationObject> entry : value.getTypes().entrySet()) {
            jgen.writeObjectField("&"+entry.getKey().name(), entry.getValue());
        }
    }
}
