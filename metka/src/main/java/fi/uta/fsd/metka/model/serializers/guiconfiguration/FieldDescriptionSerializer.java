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

package fi.uta.fsd.metka.model.serializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.enums.Language;
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
        jgen.writeBooleanField("disableRemoval", value.getDisableRemoval());
        jgen.writeObjectField("dialogTitle", value.getDialogTitle());
        jgen.writeBooleanField("showSaveInfo", value.getShowSaveInfo());
        jgen.writeBooleanField("displayHeader", value.getDisplayHeader());
        jgen.writeBooleanField("showReferenceValue", value.getShowReferenceValue());
        jgen.writeBooleanField("showReferenceType", value.getShowReferenceType());
        jgen.writeBooleanField("showReferenceSaveInfo", value.getShowReferenceSaveInfo());
        jgen.writeArrayFieldStart("showReferenceApproveInfo");
        for(Language lang : value.getShowReferenceApproveInfo()) {
            jgen.writeString(lang.toValue());
        }
        jgen.writeEndArray();
        jgen.writeBooleanField("showReferenceState", value.getShowReferenceState());
    }
}
