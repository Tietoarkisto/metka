package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.xml.sax.SAXException;

import java.io.IOException;

public class DummyIndexer extends Indexer {
    public static DummyIndexer build(DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws IOException, UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.DUMMY);
        // Check additional parameters
        if(path.getAdditionalParameters() != null && path.getAdditionalParameters().length > 0) {
            throw new UnsupportedOperationException("Dummy indexer doesn't accept additional parameters");
        }
        return new DummyIndexer(path, commands);
    }

    private DummyIndexer(DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws IOException {
        super(path, commands);
    }

    @Override
    protected void handleCommand(IndexerCommand command) throws IOException, SAXException {
        // Print some info
        System.err.println("New "+command.getPath()+" Command with action: "+command.getAction());
        // Do nothing else
    }
}
