package fi.uta.fsd.metkaSearch.directory;

//import fi.uta.fsd.metkaAmqp.Logger;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryManager {
    // Initialize logger.
    //private static final Logger log = Logger.getInstance();

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
    public static DirectoryPath formPath(boolean useRam, IndexerConfigurationType type, String language) {
        return new DirectoryPath(useRam, type, language);
    }

    public static DirectoryInformation getIndexDirectory(DirectoryPath path) throws IOException {
        DirectoryInformation index;

        index = indexDirectories.get(path);
        if(index == null) {
            index = new DirectoryInformation(path);
            indexDirectories.put(index.getPath(), index);
        }

        return index;
    }

    public static class DirectoryPath {
        private final String path;
        private final boolean useRam;
        private final IndexerConfigurationType type;
        private final String language;

        public DirectoryPath(boolean useRam, IndexerConfigurationType type, String language) {
            if(type == null) {
                throw new UnsupportedOperationException("Must have type");
            }
            this.useRam = useRam;
            this.type = type;
            this.language = language;

            StringBuilder pb = new StringBuilder();
            if(useRam) {
                pb.append("ME:");
            } else {
                pb.append("FS:");
            }
            pb.append(type);
            if(!StringUtils.isEmpty(language)) {
                pb.append("/"+language);
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

        public String getLanguage() {
            return language;
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
