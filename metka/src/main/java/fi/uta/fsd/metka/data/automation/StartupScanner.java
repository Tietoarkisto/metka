package fi.uta.fsd.metka.data.automation;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.MiscJSONRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;

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

    @PostConstruct
    public void scanForConfigurations() throws IOException {
        //File confDir = new File("src/main/resources/configuration"); // Development
        File confDir = new File("/usr/share/metka/config"); // QA-server

        Collection<File> files = FileUtils.listFiles(confDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Configuration conf = null;
            try {
                conf = json.readConfigurationFromFile(file);
            } catch(IOException ex) {
                ex.printStackTrace();
                continue;
            }
            if(conf != null) {
                configRepo.insert(conf);
            }
        }
    }

    @PostConstruct
    public void scanForMiscJSON() throws IOException {
        //File miscDir = new File("src/main/resources/misc"); // Development
        File miscDir = new File("/usr/share/metka/misc"); // QA-server

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
}