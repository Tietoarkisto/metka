package fi.uta.fsd.metka.data.automation;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.uta.fsd.metka.data.entity.ConfigurationEntity;
import fi.uta.fsd.metka.data.repository.CRUDRepository;
import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.mvc.domain.model.configuration.Configuration;
import fi.uta.fsd.metka.mvc.domain.model.configuration.ConfigurationKey;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/2/14
 * Time: 9:33 AM
 * To change this template use File | Settings | File Templates.
 */
// TODO: Implement scheduled scanning for changes and put location of new config files outside the war-file.
public class StartupScanner {
    @Autowired
    private ObjectMapper metkaObjectMapper;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @PostConstruct
    public void scanForConfigurations() throws IOException {
        File file = new File("src/main/resources/configuration");
        Collection<File> files = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".json"), TrueFileFilter.TRUE);

        for (File file1 : files) {
            file = file1;
            Configuration conf = metkaObjectMapper.readValue(file, Configuration.class);

            configurationRepository.insert(conf);
        }
    }
}
