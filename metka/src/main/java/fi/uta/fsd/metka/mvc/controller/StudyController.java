package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import fi.uta.fsd.metka.transfer.study.StudyErrorsResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
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
}
