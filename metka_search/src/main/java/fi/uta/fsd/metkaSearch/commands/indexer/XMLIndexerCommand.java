package fi.uta.fsd.metkaSearch.commands.indexer;

public class XMLIndexerCommand extends IndexerCommandBase {

    // XML-file path. This is the id of the document in index so it is required information
    private final String path;

    public XMLIndexerCommand(String path, Action action) {
        super(Type.XML, action);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
