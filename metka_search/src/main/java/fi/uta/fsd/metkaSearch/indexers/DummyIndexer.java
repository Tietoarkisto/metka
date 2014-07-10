package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.xml.sax.SAXException;

import java.io.IOException;

public class DummyIndexer extends Indexer {
    protected DummyIndexer(boolean useRam, String language) throws IOException {
        super(DirectoryManager.formPath(useRam, IndexerConfigurationType.DUMMY, language));
    }

    @Override
    protected void handleCommand(IndexerCommand command) throws IOException, SAXException {
        // Print some info
        System.err.println("New "+command.getPath()+" Command with action: "+command.getAction());
        // Do nothing else
    }
}
