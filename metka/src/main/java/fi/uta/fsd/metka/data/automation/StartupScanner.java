package fi.uta.fsd.metka.data.automation;

import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.MiscJSONRepository;
import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.model.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

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
        File confDir = new File("src/main/resources/configuration"); // Development
        //File confDir = new File("/usr/share/metka/config"); // QA-server

        Collection<File> files = FileUtils.listFiles(confDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            Configuration conf = json.readConfigurationFromFile(file);

            configRepo.insert(conf);
        }
    }

    @PostConstruct
    public void scanForMiscJSON() throws IOException {
        File miscDir = new File("src/main/resources/misc"); // Development
        //File miscDir = new File("/usr/share/metka/misc"); // QA-server

        Collection<File> files = FileUtils.listFiles(miscDir, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file : files) {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for(String line : lines) {
                sb.append(line);
            }
            JSONObject misc = json.readSimpleJSON(sb.toString());

            miscJsonRepo.insert(misc);
        }
    }
}