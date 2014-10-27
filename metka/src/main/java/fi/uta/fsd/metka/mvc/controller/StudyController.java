package fi.uta.fsd.metka.mvc.controller;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.study.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value="listErrors/{id}", method = RequestMethod.GET)
    public @ResponseBody StudyErrorListResponse listStudyErrors(@PathVariable Long id) {
        return errors.getStudyErrorList(id);
    }

    @RequestMapping(value="updateError", method = RequestMethod.POST)
    public @ResponseBody ReturnResult updateStudyError(@RequestBody StudyError error) {
        return errors.insertOrUpdateStudyError(error);
    }

    @RequestMapping(value = "removeError/{id}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removeStudyError(@PathVariable Long id) {
        return errors.removeStudyError(id);
    }

    @RequestMapping("studiesWithVariables")
    public @ResponseBody StudyVariablesStudiesResponse listStudiesWithVariables() {
        return service.collectStudiesWithVariables();
    }

    @RequestMapping("studiesWithErrors")
    public @ResponseBody StudyErrorsResponse getStudiesWithErrors() {
        return service.getStudiesWithErrors();
    }

    @RequestMapping("attachmentHistory")
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
}
