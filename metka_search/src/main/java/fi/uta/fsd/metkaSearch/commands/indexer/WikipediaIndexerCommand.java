package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

public class WikipediaIndexerCommand extends IndexerCommandBase {
    private static DirectoryManager.DirectoryPath formPath(boolean useRam, String language) {
        return DirectoryManager.formPath(useRam, IndexerConfigurationType.WIKIPEDIA, language);
    }

    // FACTORY METHODS
    public static WikipediaIndexerCommand stop(DirectoryManager.DirectoryPath path) {
        if(path.getType() != IndexerConfigurationType.WIKIPEDIA) {
            throw new UnsupportedOperationException("Path points to a different type");
        }
        return new WikipediaIndexerCommand(path, Action.STOP);
    }
    public static WikipediaIndexerCommand stop(boolean useRam, String language) {
        return new WikipediaIndexerCommand(formPath(useRam, language), Action.STOP);
    }

    /**
     * Factory method for index command on wikipedia xml-dump file.
     *
     * @param filePath Path to a wikipedia xml-dump file
     * @return WikipediaIndexerCommand to index a wikipedia xml-dump file
     */
    public static WikipediaIndexerCommand index(DirectoryManager.DirectoryPath path, String filePath) {
        if(path.getType() != IndexerConfigurationType.WIKIPEDIA) {
            throw new UnsupportedOperationException("Path points to a different type");
        }
        return new WikipediaIndexerCommand(path, filePath, Action.INDEX, null);
    }
    public static WikipediaIndexerCommand index(boolean useRam, String language, String filePath) {
        return new WikipediaIndexerCommand(formPath(useRam, language), filePath, Action.INDEX, null);
    }

    /**
     * Factory method for remove command on wikipedia page.
     *
     * @param pageId Id of the page to be removed
     * @return WikipediaIndexerCommand to remove a wikipedia page from index
     */
    public static WikipediaIndexerCommand remove(DirectoryManager.DirectoryPath path, String pageId) {
        if(path.getType() != IndexerConfigurationType.WIKIPEDIA) {
            throw new UnsupportedOperationException("Path points to a different type");
        }
        return new WikipediaIndexerCommand(path, null, Action.REMOVE, pageId);
    }
    public static WikipediaIndexerCommand remove(boolean useRam, String language, String pageId) {
        return new WikipediaIndexerCommand(formPath(useRam, language), null, Action.REMOVE, pageId);
    }

    // ACTUAL COMMAND CONTENT

    // XML-file path. This is the id of the document in index so it is required information
    private final String filePath;
    private final String pageId;

    private WikipediaIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        this(path, null, action, null);
    }

    // Command should only be formed through factory methods
    private WikipediaIndexerCommand(DirectoryManager.DirectoryPath path, String filePath, Action action, String pageId) {
        super(path, action);
        this.filePath = filePath;
        this.pageId = pageId;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getPageId() {
        return pageId;
    }
}
