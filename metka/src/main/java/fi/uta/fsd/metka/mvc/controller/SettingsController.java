package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.mvc.services.requests.UploadRequest;
import fi.uta.fsd.metka.mvc.validator.UploadRequestValidator;
import fi.uta.fsd.metkaSearch.IndexerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/settings")
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
        return "settings";
    }

    /**
     * Handles data configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadDataConfiguration", method = {RequestMethod.POST})
    public String uploadDataConfiguration(@ModelAttribute("uploadConfig")UploadRequest uploadConfig, BindingResult result)
            throws Exception {

        uploadValidator.validate(uploadConfig, result);
        if(result.hasErrors()) {
            return "settings";
        }

        service.backupAndCopy(uploadConfig.getFile(), "configuration");

        String text = new String(uploadConfig.getFile().getBytes());
        service.insertDataConfig(text);
        return "settings";
    }

    /**
     * Handles gui configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadGUIConfiguration", method = {RequestMethod.POST})
    public String uploadGUIConfiguration(@ModelAttribute("uploadConfig")UploadRequest uploadConfig, BindingResult result)
            throws Exception {

        uploadValidator.validate(uploadConfig, result);
        if(result.hasErrors()) {
            return "settings";
        }

        service.backupAndCopy(uploadConfig.getFile(), "gui");

        String text = new String(uploadConfig.getFile().getBytes());
        service.insertGUIConfig(text);
        return "settings";
    }

    /**
     * Handles configuration upload from the settings page.
     * @throws Exception
     */
    @RequestMapping(value = "uploadMiscJson", method = {RequestMethod.POST})
    public String uploadMiscJson(@ModelAttribute("uploadMisc")UploadRequest uploadMisc, BindingResult result)
            throws Exception {

        uploadValidator.validate(uploadMisc, result);
        if(result.hasErrors()) {
            return "settings";
        }

        service.backupAndCopy(uploadMisc.getFile(), "misc");

        String text = new String(uploadMisc.getFile().getBytes());
        service.insertMisc(text);
        return "settings";
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
}
