package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

public class WikipediaIndexerCommand extends IndexerCommandBase {

    private static void checkAdditionalParams(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        if(path.getAdditionalParameters() != null && path.getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("Wikipedia command doesn't accept additional parameters");
        }
    }

    // FACTORY METHODS
    /**
     * Factory method for stop command on wikipedia indexer.
     *
     * @param path Indexer path for this command
     * @return WikipediaIndexerCommand to stop wikipedia indexers
     */
    public static WikipediaIndexerCommand stop(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.WIKIPEDIA);
        checkAdditionalParams(path);
        return new WikipediaIndexerCommand(path, Action.STOP);
    }

    /**
     * Factory method for index command on wikipedia xml-dump file.
     *
     * @param path Indexer path for this command
     * @param filePath Path to a wikipedia xml-dump file
     * @return WikipediaIndexerCommand to index a wikipedia xml-dump file
     */
    public static WikipediaIndexerCommand index(DirectoryManager.DirectoryPath path, String filePath) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.WIKIPEDIA);
        checkAdditionalParams(path);
        return new WikipediaIndexerCommand(path, Action.INDEX, filePath, null);
    }

    /**
     * Factory method for remove command on wikipedia page.
     *
     * @param path Indexer path for this command
     * @param pageId Id of the page to be removed
     * @return WikipediaIndexerCommand to remove a wikipedia page from index
     */
    public static WikipediaIndexerCommand remove(DirectoryManager.DirectoryPath path, String pageId) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.WIKIPEDIA);
        checkAdditionalParams(path);
        return new WikipediaIndexerCommand(path, Action.REMOVE, null, pageId);
    }

    // ACTUAL COMMAND CONTENT

    // XML-file path. This is the id of the document in index so it is required information
    private final String filePath;
    private final String pageId;

    private WikipediaIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        this(path, action, null, null);
    }

    // Command should only be formed through factory methods
    private WikipediaIndexerCommand(DirectoryManager.DirectoryPath path, Action action, String filePath, String pageId) {
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
