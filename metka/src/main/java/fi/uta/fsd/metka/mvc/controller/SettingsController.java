package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.mvc.services.requests.UploadRequest;
import fi.uta.fsd.metka.mvc.validator.UploadRequestValidator;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.APIUserListResponse;
import fi.uta.fsd.metka.transfer.settings.NewAPIUserRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "settings")
public class SettingsController {

    @Autowired
    private UploadRequestValidator uploadValidator;

    @Autowired
    private SettingsService service;

    @Autowired
    private IndexerComponent indexer;

    @RequestMapping("")
    public String settings(Model model) {
        ModelUtil.initSettings(model, indexer.indexerStatusList());
        return AuthenticationUtil.getModelName("settings", model);
    }

    /**
     * Handles data configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadDataConfiguration", method = {RequestMethod.POST})
    public String uploadDataConfiguration(@ModelAttribute("uploadConfig")UploadRequest uploadConfig, BindingResult result, Model model)
            throws Exception {
        ModelUtil.initSettings(model, indexer.indexerStatusList());
        uploadValidator.validate(uploadConfig, result);
        if(result.hasErrors()) {
            return AuthenticationUtil.getModelName("settings", model);
        }

        service.uploadDataConfig(uploadConfig.getFile());
        return AuthenticationUtil.getModelName("settings", model);
    }

    /**
     * Handles gui configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadGUIConfiguration", method = {RequestMethod.POST})
    public String uploadGUIConfiguration(@ModelAttribute("uploadConfig")UploadRequest uploadConfig, BindingResult result, Model model)
            throws Exception {
        ModelUtil.initSettings(model, indexer.indexerStatusList());
        uploadValidator.validate(uploadConfig, result);
        if(result.hasErrors()) {
            return AuthenticationUtil.getModelName("settings", model);
        }

        service.uploadGuiConfig(uploadConfig.getFile());
        return AuthenticationUtil.getModelName("settings", model);
    }

    /**
     * Handles configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadMiscJson", method = {RequestMethod.POST})
    public String uploadMiscJson(@ModelAttribute("uploadMisc")UploadRequest uploadMisc, BindingResult result, Model model)
            throws Exception {
        ModelUtil.initSettings(model, indexer.indexerStatusList());
        uploadValidator.validate(uploadMisc, result);
        if(result.hasErrors()) {
            return AuthenticationUtil.getModelName("settings", model);
        }

        service.uploadJson(uploadMisc.getFile());
        return AuthenticationUtil.getModelName("settings", model);
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
}
