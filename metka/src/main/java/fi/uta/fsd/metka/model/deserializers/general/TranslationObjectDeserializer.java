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

package fi.uta.fsd.metka.model.deserializers.general;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.general.TranslationObject;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;

public class TranslationObjectDeserializer extends ObjectDeserializer<TranslationObject> {
    @Override
    public TranslationObject doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        TranslationObject loc = new TranslationObject();
        if(node.getNodeType() == JsonNodeType.STRING && StringUtils.hasText(node.textValue())) {
            loc.getTexts().put("default", node.textValue());
            return loc;
        } else if(node.getNodeType() == JsonNodeType.OBJECT) {
            JsonNode def = node.get("default");
            if(def != null && StringUtils.hasText(def.textValue())) {
                Iterator<String> iter = (node).fieldNames();
                while(iter.hasNext()) {
                    String lang = iter.next();
                    JsonNode text = node.get(lang);
                    if(text.getNodeType() == JsonNodeType.STRING) {
                        loc.getTexts().put(lang, text.textValue());
                    } else {
                        loc.getTexts().put(lang, "");
                    }
                }
                return loc;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
