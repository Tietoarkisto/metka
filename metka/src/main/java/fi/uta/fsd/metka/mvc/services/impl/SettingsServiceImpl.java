package fi.uta.fsd.metka.mvc.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.mvc.services.SettingsService;
import fi.uta.fsd.metka.storage.repository.APIUserRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.ReportRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.APIUserEntry;
import fi.uta.fsd.metka.transfer.settings.APIUserListResponse;
import fi.uta.fsd.metka.transfer.settings.NewAPIUserRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    @Autowired
    private APIUserRepository api;

    @Autowired
    private ReportRepository reports;

    @Value("${dir.autoload}")
    private String rootFolder;

    // User report repository to generate example report
    @Override public String generateReport() {
        // Here we could choose which report to generate based on some parameter
        return reports.gatherGeneralReport();
    }

    @Override public APIUserListResponse listAPIUsers() {
        Pair<ReturnResult, List<APIUserEntry>> result =api.listAPIUsers();
        APIUserListResponse response = new APIUserListResponse();
        response.setResult(result.getLeft());
        response.getUsers().addAll(result.getRight());
        return response;
    }

    @Override public APIUserListResponse newAPIUser(NewAPIUserRequest request) {
        Pair<ReturnResult, APIUserEntry> result = api.newAPIUser(request);
        APIUserListResponse response = new APIUserListResponse();
        response.setResult(result.getLeft());
        if(result.getRight() != null) {
            response.getUsers().add(result.getRight());
        }
        return response;
    }

    @Override public ReturnResult removeAPIUser(String publicKey) {
        return api.removeAPIUser(publicKey);
    }

    @Override
    public ReturnResult uploadConfiguration(Configuration configuration) {
        //backupAndCopy(file, "configuration");

        ReturnResult result = configurations.insert(configuration);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    @Override
    public ReturnResult uploadConfiguration(GUIConfiguration configuration) {
        //backupAndCopy(file, "gui");

        ReturnResult result = configurations.insert(configuration);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    @Override
    public ReturnResult uploadJson(JsonNode misc) {
        //backupAndCopy(file, "misc");

        ReturnResult result = miscJSONRepository.insert(misc);
        return result == ReturnResult.DATABASE_INSERT_SUCCESS ? ReturnResult.OPERATION_SUCCESSFUL : result;
    }

    /**
     * Checks the autoload folder for existing configuration file,
     * Makes a backup of it by appending LocalDate to its file name (with additional number if backup existed for the day already).
     * Then copies given MultipartFile to filesystem so that when the program starts next it will load actual up to date configuration
     * to database. With this system we might actually keep all configurations in memory in some sort of Manager class instead of bothering
     * to add them to database and constantly serialize/deserialize them to and from json.
     * @param file MultipartFile containing new configuration
     * @param folder What folder under autoload folder should the file be in
     */
    private void backupAndCopy(MultipartFile file, String folder) {
        // TODO: How to back up autoload data that is updated through ajax-call containing json and not a file...
        /*String confFolder = rootFolder+folder+"/";
        File dir = new File(confFolder);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        if(!dir.exists()) {
            // Couldn't make dir for some reason
            return;
        }

        String fileName = FilenameUtils.getName(file.getOriginalFilename());
        File location = new File(confFolder+fileName);
        if(location.exists()) {
            // Create backup file
            int bckCounter = 1; // Additional number after the filename if multiple backups have been created during the day
            File backup;
            String date = (new LocalDate()).toString();
            do {
                if(bckCounter == 1) {
                    backup = new File(confFolder+FilenameUtils.getBaseName(fileName)+"_"+date+"."+FilenameUtils.getExtension(fileName)+".old");
                } else {
                    backup = new File(confFolder+FilenameUtils.getBaseName(fileName)+"_"+date+"("+bckCounter+")."+FilenameUtils.getExtension(fileName)+".old");
                }
                bckCounter++;
            } while(backup.exists());
            location.renameTo(backup);
        }
        try {
            file.transferTo(location);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            Logger.error(SettingsServiceImpl.class, "IOException while trying to save new configuration to file " + location.getName());
        }*/
    }
}
