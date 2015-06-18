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

package fi.uta.fsd.metka.model.deserializers.guiconfiguration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.enums.ButtonType;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import fi.uta.fsd.metka.model.guiconfiguration.Button;

import java.io.IOException;

public class ButtonDeserializer extends ObjectDeserializer<Button> {

    @Override
    public Button doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        Button btn = new Button();

        // Set title
        JsonNode title = node.get("title");
        if(title == null) {
            title = node.get("&title");
        }

        TranslationObject loc = null;
        if(title != null) {
            loc = oc.treeToValue(title, TranslationObject.class);
        }
        btn.setTitle(loc);

        // Set referenceIsHandledByUser
        JsonNode isHandledByUser = node.get("isHandledByUser");
        if(isHandledByUser != null && isHandledByUser.getNodeType() == JsonNodeType.STRING) {
            btn.setIsHandledByUser(isHandledByUser.textValue());
        }

        // Set isHandler
        JsonNode handler = node.get("isHandler");
        if(handler != null && handler.getNodeType() == JsonNodeType.BOOLEAN) {
            btn.setIsHandler(handler.booleanValue());
        }

        // Set hasHandler
        handler = node.get("hasHandler");
        if(handler != null && handler.getNodeType() == JsonNodeType.BOOLEAN) {
            btn.setHasHandler(handler.booleanValue());
        }

        // Set user groups
        JsonNode groups = node.get("permissions");
        if(groups != null && groups.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode group : groups) {
                if(group.getNodeType() == JsonNodeType.STRING) {
                    btn.getPermissions().add(group.textValue());
                }
            }
        }

        // Set type
        JsonNode type = node.get("type");
        if(type != null && type.getNodeType() == JsonNodeType.STRING) {
            btn.setType(ButtonType.valueOf(type.textValue()));
        }

        // Set states
        JsonNode states = node.get("states");
        if(states != null && states.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode state : states) {
                if(state.getNodeType() == JsonNodeType.STRING) {
                    btn.getStates().add(VisibilityState.valueOf(state.textValue()));
                }
            }
        }

        JsonNode customHandler = node.get("customHandler");
        if(customHandler != null && customHandler.getNodeType() == JsonNodeType.STRING) {
            btn.setCustomHandler(customHandler.textValue());
        }

        return btn;
    }
}
