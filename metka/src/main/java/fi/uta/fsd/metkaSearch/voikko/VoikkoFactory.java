package fi.uta.fsd.metkaSearch.voikko;

import com.google.common.io.Closeables;
import org.puimula.libvoikko.Voikko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VoikkoFactory {
    private static final Logger logger = LoggerFactory.getLogger(VoikkoFactory.class);

    public static Voikko create() throws IOException {
        ClassPathResource resource = new ClassPathResource("program.properties");
        Properties p = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            p.load(inputStream);
        } catch (IOException e) {
            logger.error("Couldn't access program.properties", e);
            throw(e);
        } finally {
            Closeables.closeQuietly(inputStream);
        }
        logger.info("Creating Voikko for path "+p.getProperty("dir.dictionary"));
        try {
            return new Voikko("fi", p.getProperty("dir.dictionary"));
        } catch (Exception e) {
            logger.error("Exception while creating Voikko object.", e);
            throw e;
        }
    }
}