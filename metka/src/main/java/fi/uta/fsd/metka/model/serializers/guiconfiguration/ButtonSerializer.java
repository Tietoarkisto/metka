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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.guiconfiguration.Button;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class ButtonSerializer extends ObjectSerializer<Button> {
    @Override
    public void doSerialize(Button value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        // Write type
        if(value.getType() != null) {
            jgen.writeStringField("type", value.getType().name());
        } else {
            jgen.writeNullField("type");
        }

        if(value.getCustomHandler() != null) {
            jgen.writeStringField("customHandler", value.getCustomHandler());
        } else {
            jgen.writeNullField("customHandler");
        }

        // Write &title
        if(value.getTitle() != null) {
            jgen.writeObjectField("&title", value.getTitle());
        } else {
            jgen.writeNullField("&title");
        }

        // Write isHandledByUser
        if(StringUtils.hasText(value.getIsHandledByUser())) {
            jgen.writeStringField("isHandledByUser", value.getIsHandledByUser());
        } else {
            jgen.writeNullField("isHandledByUser");
        }

        // Write isHandler
        if(value.getIsHandler() != null) {
            jgen.writeBooleanField("isHandler", value.getIsHandler());
        } else {
            jgen.writeNullField("isHandler");
        }

        // Write isHandler
        if(value.getHasHandler() != null) {
            jgen.writeBooleanField("hasHandler", value.getHasHandler());
        } else {
            jgen.writeNullField("hasHandler");
        }

        // Write userGroups
        jgen.writeArrayFieldStart("permissions");
        for(String group : value.getPermissions()) {
            jgen.writeString(group);
        }
        jgen.writeEndArray();

        // Write states
        jgen.writeArrayFieldStart("states");
        for(VisibilityState state : value.getStates()) {
            jgen.writeString(state.name());
        }
        jgen.writeEndArray();
    }
}
