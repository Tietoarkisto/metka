package fi.uta.fsd.metka.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.serializers.configuration.FieldSerializer;
import fi.uta.fsd.metka.model.serializers.configuration.ReferenceSerializer;
import fi.uta.fsd.metka.model.serializers.configuration.SelectionListSerializer;

/**
 * Custom Jackson ObjectMapper to be used by METKA.
 * Provides configuration on how to serialize certain types and registers custom serializers.
 */
public class MetkaObjectMapper extends ObjectMapper {
    public static final long serialVersionUID = 1L;

    public MetkaObjectMapper() {
        this.registerModule(new JodaModule());
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Add serializers
        SimpleModule serializers = new SimpleModule();
        serializers.addSerializer(Field.class, new FieldSerializer());
        serializers.addSerializer(SelectionList.class, new SelectionListSerializer());
        serializers.addSerializer(Reference.class, new ReferenceSerializer());
        this.registerModule(serializers);
    }
}
