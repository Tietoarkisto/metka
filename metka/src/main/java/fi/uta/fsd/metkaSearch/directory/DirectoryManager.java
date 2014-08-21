package fi.uta.fsd.metkaSearch.directory;

//import fi.uta.fsd.metkaAmqp.Logger;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryManager {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryManager.class);

    private static final Map<DirectoryPath, DirectoryInformation> indexDirectories = new ConcurrentHashMap<>();

    // Hide default Constructor
    private DirectoryManager() {}

    /**
     * Forms a valid MetkaDirectory path.
     *
     * @param useRam
     * @param type Must not be null
     * @param language
     * @return
     */
    public static DirectoryPath formPath(boolean useRam, IndexerConfigurationType type, Language language, String... additionalParameters) {
        return new DirectoryPath(useRam, type, language, additionalParameters);
    }

    /**
     * Synchronized static method for fetching DirectoryInformation object based on path or creating one if one doesn't yet exist.
     * @param path
     * @return
     */
    public static synchronized DirectoryInformation getIndexDirectory(DirectoryPath path, boolean writable) {
        DirectoryInformation index;
        try {
            if (writable) {
                index = getWritableDirectory(path);
            } else {
                index = new DirectoryInformation(path, false);
            }
        } catch(IOException ioe) {
            logger.error("IOException while creating DirectoryInformation.");
            return null;
        }

        return index;
    }

    private static synchronized DirectoryInformation getWritableDirectory(DirectoryPath path) throws IOException {
        DirectoryInformation index = indexDirectories.get(path);
        if(index == null) {
            index = new DirectoryInformation(path, true);
            indexDirectories.put(index.getPath(), index);
        }
        return index;
    }

    /**
     * Class that handles indexer paths.
     * Functions as an id in multiple situations.
     * Paths are equal if their String representation is equal.
     */
    public static class DirectoryPath {
        private final String path;
        private final boolean useRam;
        private final IndexerConfigurationType type;
        private final Language language;
        private final String[] additionalParameters;

        /**
         * Constructor for DirectoryPath object.
         * Forms a directory path that follows the form {ME|FS}:{type}/{language}[/additionalParameters...]
         * @param useRam Should the index use RAMDirectory or FSDirectory. This forms the base of the path with ME denoting memory and FS denoting file system directory.
         * @param type Base type of the index. In most cases this will be REVISION or CONTAINER
         * @param language Language key for the index. Indexes are segregated to different directories based on their language since different languages have different
         *                 analysis requirements. Language can be null or empty in which case it's assigned the value 'default'
         * @param additionalParameters Array of additional parameters for the index location such as ConfigurationType
         */
        public DirectoryPath(boolean useRam, IndexerConfigurationType type, Language language, String... additionalParameters) {
            if(type == null) {
                throw new UnsupportedOperationException("Must have type");
            }
            this.useRam = useRam;
            this.type = type;
            this.language = language;
            this.additionalParameters = additionalParameters;

            StringBuilder pb = new StringBuilder();
            if(useRam) {
                pb.append("ME:");
            } else {
                pb.append("FS:");
            }
            pb.append(type);
            if(language != null) {
                pb.append("/");
                pb.append(language.toValue());
            } else {
                pb.append("/");
                pb.append("default");
            }

            for(String additional : additionalParameters) {
                pb.append("/");
                pb.append(additional);
            }

            path = pb.toString();
        }

        public String getPath() {
            return path;
        }

        public boolean isUseRam() {
            return useRam;
        }

        public IndexerConfigurationType getType() {
            return type;
        }

        public Language getLanguage() {
            return language;
        }

        public String[] getAdditionalParameters() {
            return additionalParameters;
        }

        @Override
        public String toString() {
            return path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DirectoryPath that = (DirectoryPath) o;

            if (!path.equals(that.path)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }
}
