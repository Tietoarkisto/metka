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
import fi.uta.fsd.metka.enums.ContainerType;
import fi.uta.fsd.metka.model.guiconfiguration.*;
import fi.uta.fsd.metka.model.serializers.ObjectSerializer;

import java.io.IOException;
import java.util.Map;

public class GUIConfigurationSerializer extends ObjectSerializer<GUIConfiguration> {
    @Override
    public void doSerialize(GUIConfiguration value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObjectField("key", value.getKey());

        jgen.writeArrayFieldStart("content");
        for(Container container : value.getContent()) {
            if(checkValidContainer(container.getType())) {
                jgen.writeObject(container);
            }
        }
        jgen.writeEndArray();

        jgen.writeArrayFieldStart("buttons");
        for(Button button : value.getButtons()) {
            jgen.writeObject(button);
        }
        jgen.writeEndArray();

        jgen.writeObjectFieldStart("fieldTitles");
        for(Map.Entry<String, FieldTitle> fieldTitle : value.getFieldTitles().entrySet()) {
            jgen.writeObjectField(fieldTitle.getKey(), fieldTitle.getValue());
        }
        jgen.writeEndObject();

        jgen.writeObjectFieldStart("dialogTitles");
        for(Map.Entry<String, DialogTitle> dialogTitle : value.getDialogTitles().entrySet()) {
            jgen.writeObjectField(dialogTitle.getKey(), dialogTitle.getValue());
        }
        jgen.writeEndObject();

        jgen.writeObjectFieldStart("subfieldConfiguration");
        for(Map.Entry<String, Container> containerEntry : value.getSubfieldConfiguration().entrySet()) {
            if(containerEntry.getValue() != null && (containerEntry.getValue().getType() == null || containerEntry.getValue().getType() == ContainerType.CELL))
                jgen.writeObjectField(containerEntry.getKey(), containerEntry.getValue());
        }
        jgen.writeEndObject();
    }

    private boolean checkValidContainer(ContainerType type) {
        // Cells and Rows are never part of content array on their own but instead always within a COLUMN container
        if(type == ContainerType.CELL) return false;
        if(type == ContainerType.EMPTYCELL) return false;
        if(type == ContainerType.ROW) return false;
        return true;
    }
}
