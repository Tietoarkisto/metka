package fi.uta.fsd.metkaAuthentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private MiscJSONRepository misc;

    @Autowired
    private JSONUtil json;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent) {
        boolean changes = false;

        Pair<ReturnResult, JsonNode> miscPair = misc.findByKey("user-list");
        JsonNode userList = null;
        if(miscPair.getLeft() != ReturnResult.MISC_JSON_FOUND) {
            userList = new ObjectNode(JsonNodeFactory.instance);
            ((ObjectNode)userList).set("key", new TextNode("user-list"));
            ((ObjectNode)userList).set("data", new ArrayNode(JsonNodeFactory.instance));
            Logger.info(LoginListener.class, "No previous user-list");
            changes = true;
        } else {
            userList = miscPair.getRight();
            Logger.info(LoginListener.class, "Previous user-list found");
        }

        MetkaAuthenticationDetails details = AuthenticationUtil.getAuthenticationDetails();

        JsonNode userNode = null;
        for(JsonNode node : ((ArrayNode)userList.get("data"))) {
            JsonNode user = node.get("userName");
            if(user != null && user.textValue().equals(details.getUserName()) ) {
                userNode = node;
                break;
            }
        }

        if(userNode == null) {
            userNode = new ObjectNode(JsonNodeFactory.instance);
            ((ObjectNode)userNode).set("userName", new TextNode(details.getUserName()));
            ((ArrayNode)userList.get("data")).add(userNode);
            changes = true;
        }

        if(!StringUtils.isEmpty(details.getDisplayName()) && (userNode.get("displayName") == null || !details.getDisplayName().equals(userNode.get("displayName").textValue()))) {
            ((ObjectNode)userNode).set("displayName", new TextNode(details.getDisplayName()));
            changes = true;
        }

        if(changes) {
            misc.insert(userList);
            Logger.info(LoginListener.class, "Updated user-list");
        }

        //Logger.info(LoginListener.class, "User login ");
    }
}
