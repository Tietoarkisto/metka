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

    @RequestMapping("")
    public String settings(Model model) {
        ModelUtil.initSettings(model);
        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value = "getJsonList/{type}", method = RequestMethod.GET)
    public @ResponseBody List<JSONListEntry> getJsonList(@PathVariable UploadJsonRequest.JsonType type) {
        return service.getJsonList(type);
    }

    @RequestMapping(value = "getJsonContent", method = RequestMethod.POST)
    public @ResponseBody String getJsonList(@RequestBody JSONListEntry entry) {
        return service.getJsonContent(entry);
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

    @RequestMapping(value="indexEverything", method = RequestMethod.GET)
    public @ResponseBody ReturnResult indexEverything() {
        return service.indexEverything();
        /*ModelUtil.initSettings(model, indexer.indexerStatusList());
        indexer.indexEverything();
        return AuthenticationUtil.getModelName("page", model);*/
    }

    @RequestMapping(value="listAPIUsers", method = RequestMethod.GET)
    public @ResponseBody APIUserListResponse listAPIUsers() {
        return service.listAPIUsers();
    }

    @RequestMapping(value="newAPIUsers", method = RequestMethod.POST)
    public @ResponseBody APIUserListResponse newAPIUsers(@RequestBody NewAPIUserRequest request) {
        return service.newAPIUser(request);
    }

    @RequestMapping(value="removeAPIUser/{key}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removeAPIUser(@PathVariable String key) {
        return service.removeAPIUser(key);
    }

    @RequestMapping(value="openIndexCommands", method = RequestMethod.GET)
    public @ResponseBody OpenIndexCommandsResponse openIndexCommands() {
        return service.getOpenIndexCommands();
    }
}
