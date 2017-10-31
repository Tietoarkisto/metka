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
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.configuration.Field;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Serializes Configuration Field including only information pertaining to the field type.
 */
public class FieldSerializer extends ObjectSerializer<Field> {
    @Override
    @SuppressWarnings("fallthrough")
    public void doSerialize(Field value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStringField("key", value.getKey());
        jgen.writeStringField("type", value.getType() != null ? value.getType().toString() : "");
        if(!(value.getType() == FieldType.REFERENCECONTAINER || value.getType() == FieldType.REFERENCE)) {
            jgen.writeBooleanField("translatable", value.getTranslatable());
        } else {
            jgen.writeBooleanField("translatable", false);
        }
        jgen.writeBooleanField("immutable", value.getImmutable());
        jgen.writeBooleanField("subfield", value.getSubfield());
        jgen.writeBooleanField("editable", value.getEditable());
        jgen.writeBooleanField("writable", value.getWritable());
        jgen.writeBooleanField("indexed", value.getIndexed());
        if(StringUtils.hasText(value.getIndexName())) {
            jgen.writeStringField("indexName", value.getIndexName());
        } else {
            jgen.writeNullField("indexName");
        }
        if(!value.getType().isContainer()) {
            jgen.writeBooleanField("generalSearch", value.getGeneralSearch());
        }

        if(!value.getType().isContainer() && value.getType() != FieldType.RICHTEXT) {
            jgen.writeBooleanField("exact", value.getExact());
        }

        switch(value.getType()) {
            case RICHTEXT:
                jgen.writeBooleanField("exact", false);
                break;
            case SELECTION:
                jgen.writeStringField("selectionList", value.getSelectionList() != null ? value.getSelectionList() : "");
                break;
            case REFERENCE:
                jgen.writeStringField("reference", value.getReference() != null ? value.getReference() : "");
                break;
            case REFERENCECONTAINER:
                jgen.writeStringField("reference", value.getReference() != null ? value.getReference() : "");
                if(StringUtils.hasText(value.getBidirectional())) {
                    jgen.writeStringField("bidirectional", value.getBidirectional() != null ? value.getBidirectional() : "");
                }
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
                jgen.writeBooleanField("fixedOrder", value.getFixedOrder());
                jgen.writeArrayFieldStart("removePermissions");
                for(String group : value.getRemovePermissions()) {
                    jgen.writeString(group);
                }
                jgen.writeEndArray();
                break;
            default:
                // Nothing to handle
                break;
        }

    }
}
