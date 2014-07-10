package fi.uta.fsd.metkaSearch.voikko;

import org.puimula.libvoikko.Voikko;

import java.io.File;

public class VoikkoFactory {

    public static Voikko create(){
        String dataPath = System.getProperty("voikko.data", "src/data/voikko");
        return new Voikko("fi", dataPath);
    }

    /**
     *
     * @param dataPath Path to a folder that contains <code>2/mor-standard/voikko-fi_FI.*</code>.
     * @return
     */
    public static Voikko create(String dataPath){
        return new Voikko("fi_FI", new File(dataPath).getAbsolutePath());
    }
}