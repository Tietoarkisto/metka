package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.ConfigurationService;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.StudyAttachmentService;
import fi.uta.fsd.metka.mvc.services.simple.ErrorMessage;
import fi.uta.fsd.metka.mvc.services.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Handles all operations related to file_management portion of Study gui.
 * Due to the nature of the gui all of these operations should be performed through AJAX.
 */
@Controller
@RequestMapping("studyAttachment")
public class StudyAttachmentController {
    @Autowired
    private StudyAttachmentService studyAttachments;

    @Autowired
    private ConfigurationService configurations;

    @Autowired
    private GeneralService general;

    /**
     * Creates a new study attachment that is attached to a study with given id.
     *
     * @return JSONObject containing append ready row in JSON format
     * @throws Exception
     */
    @RequestMapping(value = "{id}/new", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String newStudyAttachment(@PathVariable("id") Long id) throws Exception {
        return studyAttachments.newStudyAttachment(id);
    }

    /**
     * Returns newest revision for requested study attachment.
     * Should also return all needed configuration files.
     *
     * @return Json string containing newest revision data of requested study attachment and all needed configurations.
     * @throws Exception
     */
    @RequestMapping(value = "load/{id}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String loadStudyAttachment(@PathVariable("id") Long id) throws Exception {
        // TODO: implement

        Pair<ReturnResult, RevisionData> dataPair = general.getRevisionData(id, ConfigurationType.STUDY_ATTACHMENT);
        if(dataPair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Something has gone wrong, return information about that. Don't display the study attachment to user
            return null;
        }
        RevisionData revision = dataPair.getRight();
        // Get configuration for that study attachment
        // If this fails then something is seriously wrong with either the data or database
        Pair<ReturnResult, Configuration> configPair = configurations.findByTypeAndVersion(revision.getConfiguration());
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            // TODO: Couldn't get configuration, return information about that. This should deny the possibility of showing the  study attachment too since missing configuration is quite serious.
        }
        // Get possible UI configuration for study attachment
        // TODO: Return everything as a json

        return null;
    }

    /**
     * Returns the requested study attachment for given study attachment id and revision.
     * Should also return all needed configuration files.
     *
     * @return Json string containing requested study attachment revision data and all needed configurations.
     * @throws Exception
     */
    @RequestMapping(value = "load/{id}/{revision}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RevisionViewDataContainer loadStudyAttachmentRevision(@PathVariable("id") Long id, @PathVariable("revision") Integer revision) throws Exception {
        // TODO: Implement
        // Get the revision matching given parameters
        // This could fail if the study attachment doesn't exist for example

        // Get the configuration for found revision
        // If this fails then something is seriously wrong with either the data or database

        // Get possible UI configuration for study attachment
        return null;
    }

    /**
     * User requests a DRAFT revision for a study attachment with given id.
     * Checks to see that the study it's attached to is in a DRAFT state and if so
     * then either returns an existing DRAFT for requested study attachment or creates
     * a new DRAFT revision.
     * NOTICE: Study attachments are not approved by user request but instead by approving
     *         the parent study they are attached to.
     *
     * @param id Id of the study attachment requested for editing.
     * @return
     */
    @RequestMapping(value = "edit/{id}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RevisionViewDataContainer edit(@PathVariable Long id) {
        // TODO: Implement
        // Get editable revision for a study attachment with the given id
        // This can fail for multiple reasons like no study attachment existing or user not being the handler of existing DRAFT

        // Get configuration for new revision.
        // If this fails then something is seriously wrong with either the data or database

        /*RevisionViewDataContainer container = fileService.findLatestStudyAttachmentRevisionForEdit(id);*/
        return null;
    }

    /**
     * Saves changes in given study attachment data to the correct revision data.
     * If there's a change in the path of the study attachment then file link event is created for that path
     * that will check if the file is a study variable file requiring parsing.
     * NOTICE: Study attachments are not approved by user request but instead by approving
     *         the parent study they are attached to.
     *
     * @param to TransferObject containing the study attachment data from client
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "save", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ErrorMessage saveStudyAttachment(@RequestBody TransferObject to) throws Exception {
        // TODO: Implement

        // Try to save changes from given TransferObject
        // This can fail for multiple reasons, such as revision not existing or the user not being the current handler of the revision

        /*ErrorMessage result = fileService.studyAttachmentSaveAndApprove(to);
        return result;*/

        return null;
    }
}
