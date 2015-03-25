package fi.uta.fsd.metka.mvc.controller;

import codebook25.CodeBookDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.settings.JSONListEntry;
import fi.uta.fsd.metka.transfer.settings.UploadJsonRequest;
import fi.uta.fsd.metka.transfer.study.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all requests for study operations such as view and save.
 * All requests contain base address /study
 */
@Controller
@RequestMapping("study")
public class StudyController {

    @Autowired
    private StudyService service;

    @Autowired
    private StudyErrorService errors;

    @Autowired
    private JSONUtil json;

    @RequestMapping(value = "attachmentHistory", method = RequestMethod.POST)
    public @ResponseBody RevisionSearchResponse collectAttachmentHistory(@RequestBody TransferData transferData) {
        return service.collectAttachmentHistory(transferData);
    }

    @RequestMapping(value="ddi/export", method = RequestMethod.POST)
    public @ResponseBody DDIExportResponse ddiExport(@RequestBody DDIExportRequest request) {
        Pair<ReturnResult, CodeBookDocument> pair = service.exportDDI(request.getId(), request.getNo(), request.getLanguage());
        DDIExportResponse response = new DDIExportResponse();
        if(pair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            // Operation was not successful
            response.setResult(pair.getLeft());
            return response;
        }
        response.setResult(pair.getLeft());
        response.setContent(pair.getRight().toString());
        response.setId(request.getId());
        response.setNo(request.getNo());
        response.setLanguage(request.getLanguage() == Language.DEFAULT ? "fi" : request.getLanguage().toValue());
        return response;
    }

    @RequestMapping(value="ddi/import", method = RequestMethod.POST)
    public @ResponseBody ReturnResult ddiImport(@RequestBody DDIImportRequest request) {
        ReturnResult result = service.importDDI(request.getTransferData(), request.getPath());

        return result;
    }

    @RequestMapping(value="listErrors/{id}", method = RequestMethod.GET)
    public @ResponseBody StudyErrorListResponse listStudyErrors(@PathVariable Long id) {
        return errors.getStudyErrorList(id);
    }

    @RequestMapping(value = "removeError/{id}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removeStudyError(@PathVariable Long id) {
        return errors.removeStudyError(id);
    }

    @RequestMapping(value = "studiesWithErrors", method = RequestMethod.GET)
    public @ResponseBody StudyErrorsResponse getStudiesWithErrors() {
        return service.getStudiesWithErrors();
    }

    @RequestMapping(value = "studiesWithVariables", method = RequestMethod.GET)
    public @ResponseBody StudyVariablesStudiesResponse listStudiesWithVariables() {
        return service.collectStudiesWithVariables();
    }

    @RequestMapping(value="updateError", method = RequestMethod.POST)
    public @ResponseBody ReturnResult updateStudyError(@RequestBody StudyError error) {
        return errors.insertOrUpdateStudyError(error);
    }

    @RequestMapping(value = "getOrganizations", method = RequestMethod.GET)
    public @ResponseBody String getOrganizations() {
        return service.getOrganizations();
    }

    /**
     * Takes a string and a type and tries to read the string as json of the provided type.
     * If the string can be deserialized then saves it to database and to file system while making a backup of the previous file.
     * @param request
     * @return
     */
    @RequestMapping(value = "uploadOrganizations", method = RequestMethod.POST)
    public @ResponseBody ReturnResult uploadOrganizations(@RequestBody UploadJsonRequest request) {
        if(request.getType() == null || !StringUtils.hasText(request.getJson())) {
            return ReturnResult.PARAMETERS_MISSING;
        }
        switch(request.getType()) {
            case MISC: {
                Pair<SerializationResults, JsonNode> result = json.deserializeToJsonTree(request.getJson());
                if(result.getLeft() != SerializationResults.DESERIALIZATION_SUCCESS) {
                    return ReturnResult.OPERATION_FAIL;
                }
                JsonNode node = result.getRight().get("key");
                if(node == null || node.getNodeType() != JsonNodeType.STRING || !StringUtils.hasText(node.textValue()) || !node.textValue().equals("Organizations")) {
                    return ReturnResult.OPERATION_FAIL;
                }
                node = result.getRight().get("data");
                if(node == null || node.getNodeType() != JsonNodeType.ARRAY || node.size() == 0) {
                    return ReturnResult.OPERATION_FAIL;
                }
                ReturnResult r = service.uploadOrganizations(result.getRight());
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
