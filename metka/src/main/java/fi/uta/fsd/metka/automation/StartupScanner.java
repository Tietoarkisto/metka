package fi.uta.fsd.metka.automation;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

// TODO: Implement scheduled scanning for changes and put location of new config files outside the war-file.
public class StartupScanner {
    @Autowired
    private ConfigurationRepository configRepo;
    @Autowired
    private MiscJSONRepository miscJsonRepo;
    @Autowired
    private JSONUtil json;

    @Value("${dir.autoload}")
    private String rootFolder;

    /**
     * Gathers data configurations from file and saves them to database
     * @throws IOException
     */
    @PostConstruct
    public void scanForConfigurations() throws IOException {
        File confDir = new File(rootFolder+"configuration");

        Collection<File> files = FileUtils.listFiles(confDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Configuration conf = null;
            try {
                conf = json.readDataConfigurationFromFile(file);
            } catch(IOException ex) {
                ex.printStackTrace();
                continue;
            }
            if(conf != null) {
                configRepo.insert(conf);
            }
        }
    }

    /**
     * Gathers miscellaneous JSON-files from file and saves them to database
     * @throws IOException
     */
    @PostConstruct
    public void scanForMiscJSON() throws IOException {
        File miscDir = new File(rootFolder+"misc");

        Collection<File> files = FileUtils.listFiles(miscDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            JsonNode misc = null;
            try {
                misc = json.readJsonTree(file);
            } catch(IOException ex) {
                ex.printStackTrace();
                continue;
            }

            if(misc != null){
                miscJsonRepo.insert(misc);
            }
        }
    }

    /**
     * Gathers gui-configuration from file and saves them to database
     * @throws IOException
     */
    @PostConstruct
    public void scanForGUIConfigurations() throws IOException {
        File guiDir = new File(rootFolder+"gui");

        Collection<File> files = FileUtils.listFiles(guiDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            GUIConfiguration gui = null;
            try {
                gui = json.readGUIConfigurationFromFile(file);
            } catch(IOException ex) {
                ex.printStackTrace();
                continue;
            }
            if(gui != null) {
                configRepo.insert(gui);
            }
        }
    }
}