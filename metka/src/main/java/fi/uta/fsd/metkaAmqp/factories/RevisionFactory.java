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

package fi.uta.fsd.metkaAmqp.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.names.Fields;
import fi.uta.fsd.metkaAmqp.payloads.RevisionPayload;

public class RevisionFactory extends PayloadFactoryBase<RevisionPayload> {


    @Override
    public String buildRoutingKey(String resource, String event, RevisionPayload payload) {
        return super.buildRoutingKey(payload.getRevision().getConfiguration().getType().toValue().toLowerCase(), event, payload);
    }

    @Override
    public JsonNode build(String resource, String event, RevisionPayload payload) {
        ObjectNode base = (ObjectNode)super.build(payload.getRevision().getConfiguration().getType().toValue().toLowerCase(), event, payload);
        base.set("revisionable_id", new LongNode(payload.getRevision().getKey().getId()));
        base.set("revision_no", new IntNode(payload.getRevision().getKey().getNo()));
        base.set(payload.getRevision().getConfiguration().getType().toValue().toLowerCase()+"_id", getResourceId(payload));

        return base;
    }

    private JsonNode getResourceId(RevisionPayload payload) {
        ValueDataField field;
        switch(payload.getRevision().getConfiguration().getType()) {
            case STUDY: {
                field = payload.getRevision().dataField(ValueDataFieldCall.get(Fields.STUDYID)).getRight();

                if(field != null && field.hasValueFor(Language.DEFAULT)) {
                    return new TextNode(field.getActualValueFor(Language.DEFAULT));
                } else {
                    return null;
                }
            } case SERIES: {
                field = payload.getRevision().dataField(ValueDataFieldCall.get(Fields.SERIESABBR)).getRight();

                if(field != null && field.hasValueFor(Language.DEFAULT)) {
                    return new TextNode(field.getActualValueFor(Language.DEFAULT));
                } else {
                    return null;
                }
            } case PUBLICATION: {
                field = payload.getRevision().dataField(ValueDataFieldCall.get(Fields.PUBLICATIONID)).getRight();

                if(field != null && field.hasValueFor(Language.DEFAULT)) {
                    return new LongNode(field.getValueFor(Language.DEFAULT).valueAsInteger());
                } else {
                    return null;
                }
            } case STUDY_VARIABLES: {
                field = payload.getRevision().dataField(ValueDataFieldCall.get(Fields.VARFILEID)).getRight();

                if(field != null && field.hasValueFor(Language.DEFAULT)) {
                    return new TextNode(field.getActualValueFor(Language.DEFAULT));
                } else {
                    return null;
                }
            } case STUDY_VARIABLE: {
                field = payload.getRevision().dataField(ValueDataFieldCall.get(Fields.VARID)).getRight();

                if(field != null && field.hasValueFor(Language.DEFAULT)) {
                    return new TextNode(field.getActualValueFor(Language.DEFAULT));
                } else {
                    return null;
                }
            }
            default:
                return null;
        }
    }
}
