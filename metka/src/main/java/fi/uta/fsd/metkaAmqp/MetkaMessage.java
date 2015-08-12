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
import fi.uta.fsd.metka.storage.collecting.ReferenceCollector;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.reference.*;
import fi.uta.fsd.metkaAmqp.factories.PayloadFactory;
import fi.uta.fsd.metkaAmqp.payloads.PayloadObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class MetkaMessage<T extends PayloadObject> {

    protected final Reference familyExchangeRef;
    protected final Reference familyResourceRef;
    protected final Reference eventRef;
    protected final Reference eventExchangeRef;

    protected final ReferencePathRequest request;

    protected final PayloadFactory<T> factory;

    protected final String messageKey;
    protected final T payload;

    MetkaMessage(MetkaMessageType<T> type, T payload) {
        this.factory = type.getFactory();
        this.payload = payload;

        familyExchangeRef = new Reference("family_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "exchange", null);
        familyResourceRef = new Reference("resource_ref", ReferenceType.DEPENDENCY, "amqp_messages", "resource", null);
        eventRef = new Reference("message_event_ref", ReferenceType.DEPENDENCY, "amqp_messages", "events.key", "event");
        eventExchangeRef = new Reference("message_exchange_ref", ReferenceType.DEPENDENCY, "amqp_messages", "events.key", "exchange");

        this.messageKey = type.getMessage();

        request = new ReferencePathRequest();
        request.setLanguage(Language.DEFAULT);
        request.setReturnFirst(true);
        request.setRoot(new ReferencePath(new Reference("family_ref", ReferenceType.JSON, "amqp_messages", "family", null), type.getFamily()));
    }

    public void send(ReferenceCollector references, JSONUtil json, Messenger.AmqpMessenger messenger) {
        String exchange = getExchange(references);
        String resource = getResource(references);
        String event = getEvent(references);
        String routingKey = factory.buildRoutingKey(resource, event, payload);

        Pair<SerializationResults, String> payloadPair = json.serialize(factory.build(resource, event, payload));
        if(payloadPair.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
            return;
        }

        messenger.write(exchange, routingKey, payloadPair.getRight().getBytes());
        messenger.clean();
    }

    private String getExchange(ReferenceCollector references) {
        String exchange = getTitle(references, new ReferencePath(eventExchangeRef, messageKey));
        return (exchange != null ? exchange : getValue(references, familyExchangeRef));
    }

    private String getResource(ReferenceCollector references) {
        return getValue(references, familyResourceRef);
    }

    private String getEvent(ReferenceCollector references) {
        return getTitle(references, new ReferencePath(eventRef, messageKey));
    }

    private String getValue(ReferenceCollector references, Reference reference) {
        request.getRoot().setNext(new ReferencePath(reference, null));
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getValue();
    }

    private String getTitle(ReferenceCollector references, ReferencePath path) {
        request.getRoot().setNext(path);
        List<ReferenceOption> options = references.handleReferenceRequest(request).getRight();
        if(options.isEmpty()) {
            return null;
        }
        return options.get(0).getTitle().getValue();
    }
}
