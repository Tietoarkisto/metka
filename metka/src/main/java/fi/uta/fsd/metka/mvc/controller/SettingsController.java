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

package fi.uta.fsd.metka.mvc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "settings")
public class SettingsController {

    //@Autowired
    //private UploadRequestValidator uploadValidator;

    @Autowired
    private SettingsService service;

    @Autowired
    private JSONUtil json;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String settings(Model model) {
        ModelUtil.initSettings(model);
        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value="downloadReport", method = RequestMethod.GET)
    public HttpEntity<byte[]> downloadReport() {
        String report = service.generateReport();
        if(report == null) {
            // TODO: Return error to user
            return null;
        } else {
            // Assumes report.toString generates valid xml representation
            byte[] dataBytes = report.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.set("Content-Disposition",
                    "attachment; filename=report.xml");
            headers.setContentLength(dataBytes.length);

            return new HttpEntity<>(dataBytes, headers);
        }
    }

    @RequestMapping(value = "getJsonContent", method = RequestMethod.POST)
    public @ResponseBody String getJsonContent(@RequestBody JSONListEntry entry) {
        return service.getJsonContent(entry);
    }

    @RequestMapping(value = "getJsonList/{type}", method = RequestMethod.GET)
    public @ResponseBody List<JSONListEntry> getJsonList(@PathVariable UploadJsonRequest.JsonType type) {
        return service.getJsonList(type);
    }

    @RequestMapping(value="indexEverything", method = RequestMethod.GET)
    public @ResponseBody ReturnResult indexEverything() {
        return service.indexEverything();
    }

    @RequestMapping(value="listAPIUsers", method = RequestMethod.GET)
    public @ResponseBody APIUserListResponse listAPIUsers() {
        return service.listAPIUsers();
    }

    @RequestMapping(value="newAPIUser", method = RequestMethod.POST)
    public @ResponseBody APIUserListResponse newAPIUsers(@RequestBody NewAPIUserRequest request) {
        return service.newAPIUser(request);
    }

    @RequestMapping(value="openIndexCommands", method = RequestMethod.GET)
    public @ResponseBody OpenIndexCommandsResponse openIndexCommands() {
        return service.getOpenIndexCommands();
    }

    @RequestMapping(value="removeAPIUser/{userName}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removeAPIUser(@PathVariable String userName) {
        return service.removeAPIUser(userName);
    }

    @RequestMapping(value="stopIndexers", method = RequestMethod.GET)
    public @ResponseBody ReturnResult stopIndexers() {
        return service.stopIndexers();
    }

    /**
     * Takes a string and a type and tries to read the string as json of the provided type.
     * If the string can be deserialized then saves it to database and to file system while making a backup of the previous file.
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadJson", method = RequestMethod.POST)
    public @ResponseBody ReturnResult uploadJson(@RequestBody UploadJsonRequest request) {
        if(request.getType() == null || !StringUtils.hasText(request.getJson())) {
            return ReturnResult.PARAMETERS_MISSING;
        }
        switch(request.getType()) {
            case DATA_CONF: {
                Pair<SerializationResults, Configuration> result = json.deserializeDataConfiguration(request.getJson());
                if(result.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                    return ReturnResult.OPERATION_FAIL;
                }
                ReturnResult r = service.uploadConfiguration(result.getRight());
                if(r != ReturnResult.OPERATION_SUCCESSFUL) {
                    return r;
                }
                break;
            }
            case GUI_CONF: {
                Pair<SerializationResults, GUIConfiguration> result = json.deserializeGUIConfiguration(request.getJson());
                if(result.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                    return ReturnResult.OPERATION_FAIL;
                }
                ReturnResult r = service.uploadConfiguration(result.getRight());
                if(r != ReturnResult.OPERATION_SUCCESSFUL) {
                    return r;
                }
                break;
            }
            case MISC: {
                Pair<SerializationResults, JsonNode> result = json.deserializeToJsonTree(request.getJson());
                if(result.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                    return ReturnResult.OPERATION_FAIL;
                }
                JsonNode node = result.getRight().get("key");
                if(node == null || node.getNodeType() != JsonNodeType.STRING || !StringUtils.hasText(node.textValue())) {
                    return ReturnResult.OPERATION_FAIL;
                }
                node = result.getRight().get("data");
                if(node == null || node.getNodeType() != JsonNodeType.ARRAY || node.size() == 0) {
                    return ReturnResult.OPERATION_FAIL;
                }
                ReturnResult r = service.uploadJson(result.getRight());
                if(r != ReturnResult.OPERATION_SUCCESSFUL) {
                    return r;
                }
                break;
            }
            default:
                return ReturnResult.INCORRECT_TYPE_FOR_OPERATION;
        }

        return ReturnResult.OPERATION_SUCCESSFUL;
    }










}
