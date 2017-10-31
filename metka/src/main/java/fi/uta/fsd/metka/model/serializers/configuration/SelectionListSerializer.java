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
        jgen.writeStringField("default", value.getDef() != null ? value.getDef() : "");
        jgen.writeStringField("type", value.getType().toString() != null ? value.getType().toString() : "");
        jgen.writeBooleanField("includeEmpty", value.getIncludeEmpty());
        jgen.writeArrayFieldStart("freeText");
        for(String free : value.getFreeText()) {
            jgen.writeString(free != null ? free : "");
        }
        jgen.writeEndArray();
        if(StringUtils.hasText(value.getFreeTextKey())) {
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
                jgen.writeStringField("reference", value.getReference() != null ? value.getReference() : "");
                break;
            case SUBLIST:
                jgen.writeStringField("sublistKey", value.getSublistKey() != null ? value.getSublistKey() : "");
                break;
        }

    }
}
