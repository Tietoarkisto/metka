package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metka.data.repository.ConfigurationRepository;
import fi.uta.fsd.metka.data.repository.GeneralRepository;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.indexers.DummyIndexer;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.RevisionIndexer;
import fi.uta.fsd.metkaSearch.indexers.WikipediaIndexer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class IndexerComponent{

    @Autowired
    private GeneralRepository general;

    @Autowired
    private ConfigurationRepository configurations;

    // Pool for indexer threads.
    private ExecutorService indexerPool = Executors.newCachedThreadPool();

    /**
     * Map of indexers.
     * Each indexer handles a single index based on its IndexerConfigurationType
     * and takes care of commands linked to that index.
     * All objects within a single index should be parsed for indexing using similar procedures.
     * All objects within a single index should also be searchable using similar procedures.
     */
    private final Map<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> handlers = new HashMap<>();

    private final Map<DirectoryManager.DirectoryPath, Indexer> indexers = new HashMap<>();

    public void addCommand(IndexerCommand command) throws IOException {
        if(handlers.containsKey(command.getPath())) {
            if(handlers.get(command.getPath()).isDone()) {
                // Start new indexer, don't use RAMDirectory. Starting a new indexer should clear the old one from map.
                startIndexer(command.getPath());
            }
            if(!indexers.containsKey(command.getPath())) {
                // Something is wrong since indexer is not in the list but it's apparently running
                return;
            }
        } else {
            startIndexer(command.getPath());
        }
        indexers.get(command.getPath()).addCommand(command);
    }

    public void startIndexer(DirectoryManager.DirectoryPath path) throws IOException {
        if(!isIndexerRunning(path)) {
            // Remove possible stopped handlers
            clearHandlers();

            Indexer indexer = createIndexer(path);
            indexers.put(path, indexer);
            handlers.put(path, indexerPool.submit(indexer));
        }
    }

    public void clearHandlers() {
        List<DirectoryManager.DirectoryPath> remove = new ArrayList<>();
        // Get all stopped handlers
        for(Map.Entry<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> handler : handlers.entrySet()) {
            if(handler.getValue().isDone()) {
                remove.add(handler.getKey());
            }
        }

        // Remove stopped handlers and their indexers
        for(DirectoryManager.DirectoryPath path : remove) {
            Indexer indexer = indexers.get(path);
            if(indexer != null) {
                indexers.remove(path);
            }
            handlers.remove(path);
        }
    }

    public boolean hasRunningIndexers() {
        for(Map.Entry<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> handler : handlers.entrySet()) {
            if(!handler.getValue().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsIndexer(DirectoryManager.DirectoryPath path) {
        if(!StringUtils.isEmpty(path)) {
            return handlers.containsKey(path);
        }
        return false;
    }

    public boolean isIndexerRunning(DirectoryManager.DirectoryPath path) {
        if(!StringUtils.isEmpty(path) && containsIndexer(path)) {
            return !handlers.get(path).isDone();
        }
        return false;
    }

    public void stopIndexer(DirectoryManager.DirectoryPath path) {
        if(isIndexerRunning(path)) {
            handlers.get(path).cancel(true);
        }
    }

    /**
     * Returns an Indexer of the correct subclass based on the type
     * @param path
     * @return
     */
    private Indexer createIndexer(DirectoryManager.DirectoryPath path) throws IOException {
        Indexer indexer = null;
        switch(path.getType())  {
            case WIKIPEDIA:
                indexer = WikipediaIndexer.build(path);
                break;
            case DUMMY:
                indexer = DummyIndexer.build(path);
                break;
            case REVISION:
                indexer = RevisionIndexer.build(path, general, configurations);
                break;
        }
        return indexer;
    }
}
