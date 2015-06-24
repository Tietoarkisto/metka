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
import fi.uta.fsd.metka.enums.DisplayType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.deserializers.ObjectDeserializer;
import fi.uta.fsd.metka.model.guiconfiguration.DialogTitle;
import fi.uta.fsd.metka.model.guiconfiguration.FieldDescription;

import java.io.IOException;

public class FieldDescriptionDeserializer extends ObjectDeserializer<FieldDescription> {

    @Override
    public FieldDescription doDeserialize(ObjectCodec oc, JsonNode node, JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode key = node.get("key");
        FieldDescription fd = new FieldDescription(key.asText());

        // NOTICE: displayType and multichoice are defined attributes in the specification but their implementation is not an immediate concerne so they can be skipped

        // Set multiline
        JsonNode multiline = node.get("multiline");
        if(multiline != null && multiline.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setMultiline(multiline.booleanValue());
        }

        // Set column fields
        JsonNode columns = node.get("columnFields");
        if(columns != null && columns.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode column : columns) {
                if(column.getNodeType() == JsonNodeType.STRING) {
                    fd.getColumnFields().add(column.textValue());
                }
            }
        }

        // Set disableRemoval
        JsonNode disableRemoval = node.get("disableRemoval");
        if(disableRemoval != null && disableRemoval.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setDisableRemoval(disableRemoval.booleanValue());
        }

        // Set showSaveInfo
        JsonNode showSave = node.get("showSaveInfo");
        if(showSave != null && showSave.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowSaveInfo(showSave.booleanValue());
        }

        // Set showReferenceValue
        JsonNode showRef = node.get("showReferenceValue");
        if(showRef != null && showRef.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowReferenceValue(showRef.booleanValue());
        }

        // Set showReferenceSaveInfo
        JsonNode showRefSaveInfo = node.get("showReferenceSaveInfo");
        if(showRefSaveInfo != null && showRefSaveInfo.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowReferenceSaveInfo(showRefSaveInfo.booleanValue());
        }

        // Set show reference approve info
        JsonNode showRefApproveInfo = node.get("showReferenceApproveInfo");
        if(showRefApproveInfo != null && showRefApproveInfo.getNodeType() == JsonNodeType.ARRAY) {
            for(JsonNode lang : showRefApproveInfo) {
                if(lang.getNodeType() == JsonNodeType.STRING) {
                    if(Language.isLanguage(lang.textValue())) {
                        fd.getShowReferenceApproveInfo().add(Language.fromValue(lang.textValue()));
                    }
                }
            }
        }

        // Set showReferenceState
        JsonNode showRefState = node.get("showReferenceState");
        if(showRefState != null && showRefState.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setShowReferenceState(showRefState.booleanValue());
        }

        // Set displayType
        JsonNode displayType = node.get("displayType");
        if(displayType != null && displayType.getNodeType() == JsonNodeType.STRING) {
            fd.setDisplayType(DisplayType.fromValue(displayType.textValue()));
        }

        // Set dialogTitle
        JsonNode dialogTitle = node.get("dialogTitle");
        if(dialogTitle != null && dialogTitle.getNodeType() == JsonNodeType.OBJECT) {
            fd.setDialogTitle(oc.treeToValue(dialogTitle, DialogTitle.class));
        }

        // Set displayHeader
        JsonNode displayHeader = node.get("displayHeader");
        if(displayHeader != null && displayHeader.getNodeType() == JsonNodeType.BOOLEAN) {
            fd.setDisplayHeader(displayHeader.booleanValue());
        }

        return fd;
    }
}
