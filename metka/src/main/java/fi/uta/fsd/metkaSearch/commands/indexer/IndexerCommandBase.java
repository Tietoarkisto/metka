package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

public abstract class IndexerCommandBase implements IndexerCommand {
    private final DirectoryManager.DirectoryPath path;
    private final Action action;

    protected static void checkPathType(DirectoryManager.DirectoryPath path, IndexerConfigurationType type) throws UnsupportedOperationException {
        if(path.getType() != type) {
            throw new UnsupportedOperationException("Path is for a different type");
        }
    }

    protected IndexerCommandBase(DirectoryManager.DirectoryPath path, Action action) {
        this.path = path;
        this.action = action;
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    public Action getAction() {
        return action;
    }
}
