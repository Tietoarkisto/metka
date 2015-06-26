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

package fi.uta.fsd.metkaAmqp;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.enums.ReferenceType;
import fi.uta.fsd.metka.model.configuration.Reference;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.transfer.reference.*;

import java.util.List;

public abstract class MetkaMessage implements MetkaAmqpMessage {

    protected final ReferenceService references;

    private final Reference familyRef;
    protected final Reference familyExchangeRef;
    protected final Reference familyRoutingKeyRef;
    protected final Reference messageExchangeRef;
    protected final Reference messageRoutingKeyRef;
    protected final Reference messageRef;

    protected final ReferencePathRequest request;

    public MetkaMessage(ReferenceService references, String family) {
        this.references = references;

        familyRef = new Reference("family_ref", ReferenceType.JSON, "amqp_messages", "family", null);
        familyExchangeRef = new Reference("family_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "exchange", null);
        familyRoutingKeyRef = new Reference("family_routingKey_ref", ReferenceType.DEPENDENCY, "amqp_messages", "routingKey", null);
        messageExchangeRef = new Reference("message_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "messages.key", "exchange");
        messageRoutingKeyRef = new Reference("message_routingKey_ref", ReferenceType.DEPENDENCY, "amqp_messages", "messages.key", "routingKey");
        messageRef = new Reference("message_ref", ReferenceType.DEPENDENCY, "amqp_messages", "messages.key", "message");

        request = new ReferencePathRequest();
        request.setLanguage(Language.DEFAULT);
        request.setReturnFirst(true);
        request.setRoot(new ReferencePath(familyRef, family));
    }

    protected String getExchange(String key) {
        String exchange = getTitle(new ReferencePath(messageExchangeRef, key));
        return (exchange != null ? exchange : getValue(familyExchangeRef));
    }

    protected String getRoutingKey(String key) {
        String exchange = getTitle(new ReferencePath(messageRoutingKeyRef, key));
        return (exchange != null ? exchange : getValue(familyRoutingKeyRef));
    }

    protected String getMessage(String key) {
        return getTitle(new ReferencePath(messageRef, key));
    }

    protected String getValue(Reference reference) {
        request.getRoot().setNext(new ReferencePath(reference, null));
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getValue();
    }

    protected String getTitle(ReferencePath path) {
        request.getRoot().setNext(path);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getTitle().getValue();
    }
}
