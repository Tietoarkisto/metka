package fi.uta.fsd.metka.mvc.services;

import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class SettingsService {
    private static Logger logger = LoggerFactory.getLogger(SettingsService.class);
    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MiscJSONRepository miscJSONRepository;

    @Value("${dir.autoload}")
    private String rootFolder;

    public void insertDataConfig(String text) {
        configurationService.insertDataConfig(text);
    }

    public void insertGUIConfig(String text) {
        configurationService.insertGUIConfig(text);
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
    public void backupAndCopy(MultipartFile file, String folder) {
        String confFolder = rootFolder+folder+"/";
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
            logger.error("IOException while trying to save new configuration to file "+location.getName());
        }
    }

    public void insertMisc(String text) {
        miscJSONRepository.insert(text);
    }

    // User report repository to generate example report
    public Object generateReport() {
        return null;
    }
}
