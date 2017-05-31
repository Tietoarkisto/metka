package fi.uta.fsd.metkaAmqp.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import fi.uta.fsd.metkaAmqp.payloads.ApprovalDelayedPayload;

/**
 * Created by juusoko on 31.5.2017.
 * juuso.korhonen@visma.com
 */
public class ApprovalDelayedFactory extends StudyMessageFactory<ApprovalDelayedPayload>{
    @Override
    public JsonNode build(String resource, String event, ApprovalDelayedPayload payload) {
        ObjectNode base = (ObjectNode)super.build(resource, event, payload);
        base.set("condition", new TextNode(payload.getCondition()));
        base.set("updated_at", new TextNode(payload.getUpdated_at()));
        return base;
    }
}
