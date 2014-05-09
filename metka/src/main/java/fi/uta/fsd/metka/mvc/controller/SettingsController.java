package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.SettingsService;
import fi.uta.fsd.metka.mvc.domain.requests.UploadRequest;
import fi.uta.fsd.metka.mvc.validator.UploadRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @RequestMapping("")
    public String settings(@ModelAttribute("uploadConfig")UploadRequest uploadConfig,
                           @ModelAttribute("uploadMisc")UploadRequest uploadMisc) {
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

        String text = new String(uploadMisc.getFile().getBytes());
        service.insertMisc(text);
        return "settings";
    }
}
