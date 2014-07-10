package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

import java.io.IOException;

public class DummyIndexerCommand extends IndexerCommandBase {
    private static void checkAdditionalParams(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        if(path.getAdditionalParameters() != null && path.getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("Dummy command doesn't accept additional parameters");
        }
    }

    public static DummyIndexerCommand stop(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.DUMMY);
        checkAdditionalParams(path);
        return new DummyIndexerCommand(path, Action.STOP);
    }

    public static DummyIndexerCommand index(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.DUMMY);
        checkAdditionalParams(path);
        return new DummyIndexerCommand(path, Action.INDEX);
    }

    public static DummyIndexerCommand remove(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.DUMMY);
        checkAdditionalParams(path);
        return new DummyIndexerCommand(path, Action.REMOVE);
    }

    private DummyIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        super(path, action);
    }
}
