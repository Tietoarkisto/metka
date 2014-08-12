package fi.uta.fsd.metkaSearch.voikko;

import org.puimula.libvoikko.Voikko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoikkoFactory {
    private static final Logger logger = LoggerFactory.getLogger(VoikkoFactory.class);

    public static Voikko create() {
        String dataPath = System.getProperty("voikko.data", "src/data/voikko");
        logger.info("Creating Voikko for path "+dataPath);
        try {
            return new Voikko("fi", dataPath);
        } catch (Exception e) {
            logger.error("Exception while creating Voikko object, assume that dictionary is not found and use file system location instead");
            // TODO: Move to some property format
            return new Voikko("fi", "/usr/share/metka/voikko");
        }
    }
}