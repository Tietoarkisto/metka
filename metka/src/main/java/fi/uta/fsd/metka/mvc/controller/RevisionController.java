package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("revision")
public class RevisionController {
    private static final Logger logger = LoggerFactory.getLogger(RevisionController.class);
    @Autowired
    private RevisionService revisions;

    @RequestMapping("search/{type}")
    public String search(@PathVariable ConfigurationType type, Model model) {
        // Take away types that shouldn't navigate through here
        switch(type) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, type);

        return AuthenticationUtil.getModelName("revision", model);
    }

    @RequestMapping("view/{type}/{id}")
    public String viewLatestRevision(@PathVariable ConfigurationType type, @PathVariable Long id, Model model) {
        // Take away types that shouldn't navigate through here
        switch(type) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, type, id);

        return AuthenticationUtil.getModelName("revision", model);
    }

    @RequestMapping("view/{type}/{id}/{no}")
    public String viewRevision(@PathVariable ConfigurationType type, @PathVariable Long id, @PathVariable Integer no, Model model) {
        // Take away types that shouldn't navigate through here
        switch(type) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, type, id, no);

        return AuthenticationUtil.getModelName("revision", model);
    }

    @RequestMapping(value = "ajax/configuration/{type}", method = RequestMethod.GET)
    public @ResponseBody ConfigurationResponse getConfiguration(@PathVariable ConfigurationType type) {
        return revisions.getConfiguration(type);
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
    public @ResponseBody RevisionDataResponse ajaxViewLatestRevisionWithType(@PathVariable ConfigurationType type, @PathVariable Long id) {
        return revisions.view(id, type);
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
    public @ResponseBody RevisionDataResponse ajaxViewRevisionWithType(@PathVariable ConfigurationType type, @PathVariable Long id, @PathVariable Integer no) {
        return revisions.view(id, no, type);
    }

    @RequestMapping(value="ajax/create", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse create(@RequestBody RevisionCreateRequest request) {
        RevisionOperationResponse response = revisions.create(request);
        if(response.getResult().equals(ReturnResult.REVISION_CREATED.name())) {
            RevisionOperationResponse claimResponse = revisions.claimRevision(response.getData().getKey());
            if(claimResponse.getResult().equals(ReturnResult.REVISION_UPDATE_SUCCESSFUL.name())) {
                claimResponse.setResult(response.getResult());
                return claimResponse;
            } else {
                return response;
            }
        } else return response;
    }

    @RequestMapping(value="ajax/edit", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse edit(@RequestBody TransferData transferData) {
        return revisions.edit(transferData);
    }

    @RequestMapping(value="ajax/remove", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse remove(@RequestBody TransferData transferData) {
        return revisions.remove(transferData);
    }

    @RequestMapping(value="ajax/restore", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse restore(@RequestBody TransferData transferData) {
        return revisions.restore(transferData);
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
            response.setResult(ReturnResult.EXCEPTION.name());
            return response;
        }
    }

    @RequestMapping(value="ajax/approve", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse approve(@RequestBody TransferData transferData) {
        return revisions.approve(transferData);
    }

    @RequestMapping(value="ajax/search", method = RequestMethod.POST)
    public @ResponseBody RevisionSearchResponse search(@RequestBody RevisionSearchRequest searchRequest) {
        return revisions.search(searchRequest);
    }

    @RequestMapping(value = "ajax/claim", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionOperationResponse claim(@RequestBody RevisionKey key) {
        return revisions.claimRevision(key);
    }

    @RequestMapping(value = "ajax/release", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionOperationResponse release(@RequestBody RevisionKey key) {
        return revisions.releaseRevision(key);
    }

    @RequestMapping(value = "studyIdSearch/{studyId}", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionSearchResponse studyIdSearch(@PathVariable String studyId) {
        return revisions.studyIdSearch(studyId);
    }

    @RequestMapping(value = "revisionHistory", method = RequestMethod.POST)
    public @ResponseBody RevisionSearchResponse collectAttachmentHistory(@RequestBody RevisionHistoryRequest request) {
        return revisions.collectRevisionHistory(request);
    }

    @RequestMapping(value = "revisionCompare", method = RequestMethod.POST)
    public @ResponseBody RevisionCompareResponse revisionCompare(@RequestBody RevisionCompareRequest request) {
        return revisions.revisionCompare(request);
    }
}