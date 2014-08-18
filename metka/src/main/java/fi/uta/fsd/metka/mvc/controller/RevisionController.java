package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/revision")
public class RevisionController {
    private static final Logger logger = LoggerFactory.getLogger(RevisionController.class);
    @Autowired
    private RevisionService revisions;

    @RequestMapping("search/{type}")
    public String search(@PathVariable String type, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, ct);

        return "search";
    }

    @RequestMapping("view/{type}/{id}")
    public String viewLatestRevision(@PathVariable String type, @PathVariable Long id, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, ct, id);

        return "view";
    }

    @RequestMapping("view/{type}/{id}/{no}")
    public String viewRevision(@PathVariable String type, @PathVariable Long id, @PathVariable Integer no, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, ct, id, no);

        return "view";
    }

    /**
     * Returns latest revision data and related configurations.
     * This operation checks that data is of requested type.
     *
     * @param id RevisionableId of the requested revision
     * @param type ConfigurationType that the requested revision should be
     * @return RevisionDataResponse object containing the revision data as TransferData, Configuration with specific version and the newest GUIConfiguration for the revision type
     */
    @RequestMapping(value = "ajax/view/{type}/{id}", method = RequestMethod.GET)
    public @ResponseBody RevisionDataResponse ajaxViewLatestRevisionWithType(@PathVariable String type, @PathVariable Long id) {
        return revisions.view(id, type.toUpperCase());
    }

    /**
     * Returns a revision data and related configurations.
     * This operation checks that data is of requested type.
     *
     * @param id RevisionableId of the requested revision
     * @param no Revision number of the requested revision
     * @param type ConfigurationType that the requested revision should be
     * @return RevisionDataResponse object containing the revision data as TransferData, Configuration with specific version and the newest GUIConfiguration for the revision type
     */
    @RequestMapping(value = "ajax/view/{type}/{id}/{no}", method = RequestMethod.GET)
    public @ResponseBody RevisionDataResponse ajaxViewRevisionWithType(@PathVariable String type, @PathVariable Long id, @PathVariable Integer no) {
        return revisions.view(id, no, type.toUpperCase());
    }

    @RequestMapping(value="ajax/create", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse create(@RequestBody RevisionCreateRequest request) {
        return revisions.create(request);
    }

    @RequestMapping(value="ajax/edit", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse edit(@RequestBody TransferData transferData) {
        return revisions.edit(transferData);
    }

    @RequestMapping(value="ajax/save", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse save(@RequestBody TransferData transferData) {
        RevisionOperationResponse response = null;
        try {
            response = revisions.save(transferData);
            return response;
        } catch(Exception e) {
            logger.error("Exception while performing SAVE command on revision:", e);
            response = new RevisionOperationResponse();
            response.setData(transferData);
            response.setResult(ReturnResult.EXCEPTION);
            return response;
        }
    }

    @RequestMapping(value="ajax/approve", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse approve(@RequestBody TransferData transferData) {
        return revisions.approve(transferData);
    }

    @RequestMapping(value="ajax/search", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionSearchResponse search(@RequestBody RevisionSearchRequest searchRequest) {
        return revisions.search(searchRequest);
    }

    @RequestMapping(value = "studyIdSearch/{studyId}", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionSearchResponse studyIdSearch(@PathVariable String studyId) {
        return revisions.studyIdSearch(studyId);
    }
}