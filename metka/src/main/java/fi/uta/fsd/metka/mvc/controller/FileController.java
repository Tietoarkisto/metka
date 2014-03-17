package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.mvc.domain.ConfigurationService;
import fi.uta.fsd.metka.mvc.domain.FileService;
import fi.uta.fsd.metka.mvc.domain.simple.transfer.TransferObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Handles all operations related to file_management portion of Study gui.
 * Due to the nature of the gui all of these operations should be performed through AJAX.
 */
@Controller
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @Autowired
    private ConfigurationService configService;

    /**
     * Returns current File-configuration as a JSON-construct.
     * @return Configuration
     * @throws Exception
     */
    @RequestMapping(value = "currentConfiguration", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Configuration getCurrentFileConfiguration() throws Exception {
        Configuration config = configService.findLatestByType(ConfigurationType.FILE);
        return config;
    }

    /**
     * Returns current File-configuration as a JSON-construct.
     * @return Configuration
     * @throws Exception
     */
    @RequestMapping(value = "save", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Integer save(@RequestBody TransferObject to) throws Exception {
        Integer revisionableId = fileService.addFile(to);
        return revisionableId;
    }

    /**
     * Handles file upload from ajax call.
     * Returns header information for the file in a append ready row json.
     * @return JSONObject containing append ready row in JSON format
     * @throws Exception
     */
    @RequestMapping(value = "upload", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String upload(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id, @RequestParam("targetField") String key) throws Exception {
        /*Iterator<String> itr = request.getFileNames();
        MultipartFile file = request.getFile(itr.next());*/
        try {
            String fileName = new File(file.getOriginalFilename()).getName();
            String path = fileService.saveFile(file, fileName, id);
            String fileRow = fileService.initNewFile(path, id, key);
            return fileRow;
        } catch(IOException ex) {
            // TODO: Return error
            ex.printStackTrace();
            return null;
        }
    }
}
