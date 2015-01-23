package fi.uta.fsd.metkaSearch.voikko;

import com.google.common.io.Closeables;
import fi.uta.fsd.Logger;
import org.puimula.libvoikko.Voikko;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VoikkoFactory {

    public static Voikko create() throws IOException {
        ClassPathResource resource = new ClassPathResource("program.properties");
        Properties p = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            p.load(inputStream);
        } catch (IOException e) {
            Logger.error(Voikko.class, "Couldn't access program.properties", e);
            throw(e);
        } finally {
            Closeables.closeQuietly(inputStream);
        }
        Logger.debug(Voikko.class, "Creating Voikko for path " + p.getProperty("dir.dictionary"));
        try {
            return new Voikko("fi", p.getProperty("dir.dictionary"));
        } catch (Exception e) {
            Logger.error(Voikko.class, "Exception while creating Voikko object.", e);
            throw e;
        }
    }
}