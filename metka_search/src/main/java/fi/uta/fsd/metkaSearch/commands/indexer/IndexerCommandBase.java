package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;

public abstract class IndexerCommandBase implements IndexerCommand {
    private final DirectoryManager.DirectoryPath path;
    private final Action action;

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
