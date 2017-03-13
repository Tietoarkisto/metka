package fi.uta.fsd.metkaAmqp.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import fi.uta.fsd.metkaAmqp.payloads.ContractTriggerPayload;

/**
 * Created by juusoko on 8.3.2017.
 * juuso.korhonen@visma.com
 */
public class ContractTriggerFactory extends StudyMessageFactory<ContractTriggerPayload> {
    @Override
    public JsonNode build(String resource, String event, ContractTriggerPayload payload) {
        ObjectNode base = (ObjectNode)super.build(resource, event, payload);
        base.set("triggerdate", new TextNode(payload.getTriggerDate()));
        base.set("receiver", new TextNode(payload.getReceiver()));
        base.set("label", new TextNode(payload.getLabel()));
        return base;
    }
}
