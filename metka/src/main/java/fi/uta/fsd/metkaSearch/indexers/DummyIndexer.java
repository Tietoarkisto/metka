package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

public class DummyIndexer extends Indexer {
    public static DummyIndexer build(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.DUMMY);
        // Check additional parameters
        if(manager == null) {
            throw new UnsupportedOperationException("Needs a DirectoryManager");
        }
        if(path.getAdditionalParameters() != null && path.getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("Dummy indexer doesn't accept additional parameters");
        }
        return new DummyIndexer(manager, path, commands);
    }

    private DummyIndexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws UnsupportedOperationException {
        super(manager, path, commands);
    }

    @Override
    protected void handleCommand(IndexerCommand command) {
        // Print some info
        System.err.println("New "+command.getPath()+" Command with action: "+command.getAction());
        // Do nothing else
    }
}
