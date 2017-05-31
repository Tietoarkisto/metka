package fi.uta.fsd.metkaAmqp.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import fi.uta.fsd.metkaAmqp.payloads.ErrorTriggerPayload;

/**
 * Created by juusoko on 30.5.2017.
 * juuso.korhonen@visma.com
 */
public class ErrorTriggerFactory extends StudyMessageFactory<ErrorTriggerPayload> {
    @Override
    public JsonNode build(String resource, String event, ErrorTriggerPayload payload) {
        ObjectNode base = (ObjectNode)super.build(resource, event, payload);
        base.set("triggerdate", new TextNode(payload.getTriggerdate()));
        base.set("receiver", new TextNode(payload.getReceiver()));
        base.set("label", new TextNode(payload.getLabel()));
        return base;
    }
}