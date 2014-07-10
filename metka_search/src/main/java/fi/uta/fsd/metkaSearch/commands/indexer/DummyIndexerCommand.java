package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

import java.io.IOException;

public class DummyIndexerCommand extends IndexerCommandBase {
    private static DirectoryManager.DirectoryPath formPath(boolean useRam, String language) {
        return DirectoryManager.formPath(useRam, IndexerConfigurationType.DUMMY, language);
    }
    public static DummyIndexerCommand index(boolean useRam, String language) throws IOException {
        return new DummyIndexerCommand(formPath(useRam, language), Action.INDEX);
    }

    public static DummyIndexerCommand remove(boolean useRam, String language) throws IOException {
        return new DummyIndexerCommand(formPath(useRam, language), Action.REMOVE);
    }

    private DummyIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        super(path, action);
    }
}
