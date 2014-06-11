package fi.uta.fsd.metka.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.configuration.Option;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.model.configuration.SelectionList;
import fi.uta.fsd.metka.model.deserializers.configuration.OptionDeserializer;
import fi.uta.fsd.metka.model.deserializers.general.TranslationObjectDeserializer;
import fi.uta.fsd.metka.model.deserializers.guiconfiguration.ButtonDeserializer;
import fi.uta.fsd.metka.model.deserializers.guiconfiguration.ContainerDeserializer;
import fi.uta.fsd.metka.model.deserializers.guiconfiguration.FieldDescriptionDeserializer;
import fi.uta.fsd.metka.model.deserializers.guiconfiguration.FieldTitleDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.*;
import fi.uta.fsd.metka.model.serializers.configuration.FieldSerializer;
import fi.uta.fsd.metka.model.serializers.configuration.OptionSerializer;
import fi.uta.fsd.metka.model.serializers.configuration.ReferenceSerializer;
import fi.uta.fsd.metka.model.serializers.configuration.SelectionListSerializer;
import fi.uta.fsd.metka.model.serializers.general.TranslationObjectSerializer;
import fi.uta.fsd.metka.model.serializers.guiconfiguration.*;

/**
 * Custom Jackson ObjectMapper to be used by METKA.
 * Provides configuration on how to serialize certain types and registers custom serializers.
 */
public class MetkaObjectMapper extends ObjectMapper {
    public static final long serialVersionUID = 1L;

    public MetkaObjectMapper() {
        this.registerModule(new JodaModule());
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Add serializers and deserializers
        SimpleModule parsers = new SimpleModule();

        // Add general
        parsers.addDeserializer(TranslationObject.class, new TranslationObjectDeserializer());
        parsers.addSerializer(TranslationObject.class, new TranslationObjectSerializer());

        // Gui config
        parsers.addSerializer(GUIConfiguration.class, new GUIConfigurationSerializer());

        parsers.addDeserializer(Container.class, new ContainerDeserializer());
        parsers.addSerializer(Container.class, new ContainerSerializer());

        parsers.addDeserializer(FieldDescription.class, new FieldDescriptionDeserializer());
        parsers.addSerializer(FieldDescription.class, new FieldDescriptionSerializer());

        parsers.addDeserializer(FieldTitle.class, new FieldTitleDeserializer());
        parsers.addSerializer(FieldTitle.class, new FieldTitleSerializer());

        parsers.addDeserializer(Button.class, new ButtonDeserializer());
        parsers.addSerializer(Button.class, new ButtonSerializer());

        // Data config
        parsers.addDeserializer(Option.class, new OptionDeserializer());
        parsers.addSerializer(Option.class, new OptionSerializer());

        parsers.addSerializer(Field.class, new FieldSerializer());
        parsers.addSerializer(SelectionList.class, new SelectionListSerializer());
        parsers.addSerializer(Reference.class, new ReferenceSerializer());

        // Register module
        this.registerModule(parsers);
    }
}
