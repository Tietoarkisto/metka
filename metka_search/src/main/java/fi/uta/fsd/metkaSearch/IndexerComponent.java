package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommandBase;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class IndexerComponent {
    // Pool for indexer threads.
    // It might be beneficial to have a pool with one thread per index so that multiple types of data could be
    // indexed at the same time
    private ExecutorService indexerPool = Executors.newFixedThreadPool(1);

    // Command queue for indexer commands
    private final BlockingQueue<IndexerCommandBase> commandQueue = new LinkedBlockingQueue<>();

    public int queueSize() {
        return commandQueue.size();
    }

    public void addCommand(IndexerCommandBase command) {
        commandQueue.add(command);
    }
}
