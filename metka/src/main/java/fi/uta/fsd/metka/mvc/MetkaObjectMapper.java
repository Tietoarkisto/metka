/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

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
import fi.uta.fsd.metka.model.deserializers.guiconfiguration.*;
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

        parsers.addDeserializer(DialogTitle.class, new DialogTitleDeserializer());
        parsers.addSerializer(DialogTitle.class, new DialogTitleSerializer());

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
