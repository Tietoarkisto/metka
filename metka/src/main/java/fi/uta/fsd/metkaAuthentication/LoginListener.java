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
            Logger.info(getClass(), "No previous user-list");
            changes = true;
        } else {
            userList = miscPair.getRight();
            Logger.info(getClass(), "Previous user-list found");
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
            Logger.info(getClass(), "Updated user-list");
        }
    }
}
