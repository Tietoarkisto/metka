package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import fi.uta.fsd.metka.transfer.study.StudyVariablesStudiesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all requests for study operations such as view and save.
 * All requests contain base address /study
 */
@Controller
@RequestMapping("/study")
public class StudyController {

    @Autowired
    private StudyService service;

    @Autowired
    private StudyErrorService errors;

    // TODO: Add study specific ajax calls like binder list, errors list and error operations (add, modify, remove)
    @RequestMapping(value="listErrors/{id}/{no}", method = RequestMethod.GET)
    public @ResponseBody StudyErrorListResponse listStudyErrors(@PathVariable Long id, @PathVariable Integer no) {
        return errors.getStudyErrorList(id, no);
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

    // TODO: Collect single study attachment revision history
}
