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

public abstract class MetkaMessage {

    private final Reference familyRef;
    protected final Reference familyExchangeRef;
    protected final Reference familyResourceRef;
    protected final Reference eventRef;
    protected final Reference eventExchangeRef;

    protected final ReferencePathRequest request;

    protected final PayloadFactory payload;

    protected final String messageKey;

    public MetkaMessage(PayloadFactory payload, String family, String messageKey) {
        this.payload = payload;

        familyRef = new Reference("family_ref", ReferenceType.JSON, "amqp_messages", "family", null);
        familyExchangeRef = new Reference("family_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "exchange", null);
        familyResourceRef = new Reference("resource_ref", ReferenceType.DEPENDENCY, "amqp_messages", "resource", null);
        eventRef = new Reference("message_event_ref", ReferenceType.DEPENDENCY, "amqp_messages", "events.key", "event");
        eventExchangeRef = new Reference("message_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "events.key", "exchange");

        this.messageKey = messageKey;

        request = new ReferencePathRequest();
        request.setLanguage(Language.DEFAULT);
        request.setReturnFirst(true);
        request.setRoot(new ReferencePath(familyRef, family));
    }

    public void send(ReferenceService references, Messenger.AmqpMessenger messenger) {
        String exchange = getExchange(references);
        String routingKey = buildRoutingKey(references);

        messenger.write(exchange, routingKey, payload.build(this));
    }

    public abstract String buildRoutingKey(ReferenceService references);

    protected String getExchange(ReferenceService references) {
        String exchange = getTitle(references, new ReferencePath(eventExchangeRef, messageKey));
        return (exchange != null ? exchange : getValue(references, familyExchangeRef));
    }

    public String getResource(ReferenceService references) {
        return getValue(references, familyResourceRef);
    }

    public String getEvent(ReferenceService references) {
        return getTitle(references, new ReferencePath(eventRef, messageKey));
    }

    private String getValue(ReferenceService references, Reference reference) {
        request.getRoot().setNext(new ReferencePath(reference, null));
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getValue();
    }

    private String getTitle(ReferenceService references, ReferencePath path) {
        request.getRoot().setNext(path);
        List<ReferenceOption> options = references.collectReferenceOptions(request);
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getTitle().getValue();
    }
}
