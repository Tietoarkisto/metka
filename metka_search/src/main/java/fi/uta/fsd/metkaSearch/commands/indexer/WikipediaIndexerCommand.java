package fi.uta.fsd.metkaSearch.commands.indexer;

public class WikipediaIndexerCommand extends IndexerCommandBase {

    // XML-file path. This is the id of the document in index so it is required information
    private final String path;
    private String pageId;

    public WikipediaIndexerCommand(String path, Action action) {
        super(Type.WIKIPEDIA, action);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}
