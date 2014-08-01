package fi.uta.fsd.metka.automation;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.MiscJSONRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;

public class StartupScanner {
    private static Logger logger = LoggerFactory.getLogger(StartupScanner.class);
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
     */
    @PostConstruct
    public void scanForConfigurations() {
        File confDir = new File(rootFolder+"configuration");

        Collection<File> files = FileUtils.listFiles(confDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<ReturnResult, Configuration> conf = json.deserializeDataConfiguration(file);
            if(conf.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Failed at deserializing "+file.getName());
                continue;
            }
            configRepo.insert(conf.getRight());
        }
    }

    /**
     * Gathers miscellaneous JSON-files from file and saves them to database
     */
    @PostConstruct
    public void scanForMiscJSON() {
        File miscDir = new File(rootFolder+"misc");

        Collection<File> files = FileUtils.listFiles(miscDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<ReturnResult, JsonNode> misc = json.deserializeToJsonTree(file);

            if(misc.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS){
                logger.error("Failed at deserializing "+file.getName());
                continue;
            }
            miscJsonRepo.insert(misc.getRight());
        }
    }

    /**
     * Gathers gui-configuration from file and saves them to database
     */
    @PostConstruct
    public void scanForGUIConfigurations() {
        File guiDir = new File(rootFolder+"gui");

        Collection<File> files = FileUtils.listFiles(guiDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Pair<ReturnResult, GUIConfiguration> gui = json.deserializeGUIConfiguration(file);

            if(gui.getLeft() != ReturnResult.DESERIALIZATION_SUCCESS) {
                logger.error("Failed at deserializing "+file.getName());
                continue;
            }
            configRepo.insert(gui.getRight());
        }
    }
}