package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.XMLIndexerCommand;
import fi.uta.fsd.metkaSearch.information.DirectoryInformation;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class IndexerComponent {
    public static final Version USED_VERSION = Version.LUCENE_48;

    // Pool for indexer threads.
    // It might be beneficial to have a pool with one thread per index so that multiple types of data could be
    // indexed at the same time
    private ExecutorService indexerPool = Executors.newFixedThreadPool(1);
    // Indexer command handling thread. Runs until interrupted.
    // Will periodically flush written index to disk so that reader can get updates
    private final Future<Integer> commandHandler = handleCommands();

    /**
     * Defines how many idle loops (i.e. loops with no new commands) must happen
     * before changes are flushed to disk.
     */
    private static final int IDLE_LOOPS_BEFORE_FLUSH = 3;
    /**
     * Defines if indexing will take a break after multiple commands to flush changes to disk.
     */
    private static boolean FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS = true;
    /**
     * Defines maximum number of commands to handle between flushes if FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS is true.
     */
    private static final int MAX_COMMAND_BATCH_SIZE = 5;

    // Command queue for indexer commands
    private final BlockingQueue<IndexerCommand> commandQueue = new LinkedBlockingQueue<>();

    public int queueSize() {
        return commandQueue.size();
    }

    public void addCommand(IndexerCommand command) {
        commandQueue.add(command);
    }

    public boolean isHandlerRunning() {
        return !commandHandler.isDone();
    }

    public void stopCommandHandler() {
        commandHandler.cancel(true);
    }

    private Future<Integer> handleCommands() {
        return indexerPool.submit(new Callable<Integer>() {
            // Running status for the handler loop
            private boolean running = true;

            // Switch to see if there has been indexed data that needs to be flushed to disk
            private boolean indexChanged = false;

            // Counter for idle loops. If there's been changes to index they will be flushed after certain number of times.
            private int idleLoops = 0;
            // Counter for processed commands. Used to detect when to force flush the index if that setting is true.
            private int commandBatch = 0;

            private DirectoryInformation indexer;

            @Override
            public Integer call() throws Exception {
                indexer = LuceneFactory.getInMemoryIndexWriter();

                while(running) {
                    try {
                        IndexerCommand command = commandQueue.poll(5, TimeUnit.SECONDS);
                        if(command != null) {
                            // Reset idle loops since there's work
                            idleLoops = 0;

                            // Handle command
                            handleCommand(command);

                            // Set indexChanged to true since command was handled
                            indexChanged = true;
                            if(FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS) {
                                commandBatch++;
                            }
                        } else {
                            // Increase idleLoops counter if index has changed
                            if(indexChanged) idleLoops++;
                            System.err.println("Number of idle loops: "+idleLoops);
                        }

                        if((idleLoops >= IDLE_LOOPS_BEFORE_FLUSH)
                                || (FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS && commandBatch >= MAX_COMMAND_BATCH_SIZE)) {
                            flushIndex();
                        }

                        // Check if process should continue running
                        if(Thread.currentThread().isInterrupted()) {
                            running = false;
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        throw new InterruptedException();
                    }
                }
                return 0;
            }

            private void handleCommand(IndexerCommand command) throws IOException {
                System.err.println("New "+command.getType()+" Command with action: "+command.getAction());
                switch(command.getType()) {
                    case XML:
                        handleXMLCommand((XMLIndexerCommand)command);
                        break;
                }
            }

            private void handleXMLCommand(XMLIndexerCommand command) throws IOException {
                switch(command.getAction()) {
                    case REMOVE:
                        // Create term for identification
                        Term term = new Term("id_path", command.getPath());
                        removeDocument(term);
                        break;
                    case INDEX:
                        indexXMLCommand(command);
                        break;
                }
            }

            /**
             * Create Document out of an XML file and add it to the writer.
             *
             * @param command
             */
            private void indexXMLCommand(XMLIndexerCommand command) throws IOException {
                // Get the XML-file and parse it to some format for processing

                // Create analyzers map for PerFieldAnalyzerWrapper
                Map<String, Analyzer> analyzers = new HashMap<>();

                // Create Document
                Document document = new Document();
                // add XML-file path as a field to the
                document.add(new StringField("id_path", command.getPath(), Field.Store.NO));

                // Add fields from the XML-file to index sometimes including a different analyzer

                // Create analyzer wrapper
                PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new WhitespaceAnalyzer(USED_VERSION), analyzers);

                // Add document to index with analyzer wrapper, possibly prepare for OutOfMemoryError
                indexer.getIndexWriter().addDocument(document, analyzer);
            }

            private void removeDocument(Term term) throws IOException {
                // TODO: OutOfMemory checks and writer closing
                indexer.getIndexWriter().deleteDocuments(term);
            }

            private void flushIndex() throws IOException {
                indexChanged = false;
                idleLoops = 0;
                commandBatch = 0;
                if(indexer != null) {
                    try {
                        // Try to commit the writer
                        indexer.getIndexWriter().commit();
                    } catch (OutOfMemoryError er) {
                        er.printStackTrace();
                        // If we get an OutOfMemoryError then close the writer immediately
                        try {
                            // Try closing the writer
                            indexer.getIndexWriter().close();
                        } catch(OutOfMemoryError erc) {
                            // As I understand it we should get another OutOfMemoryError, close the writer again
                            indexer.getIndexWriter().close();
                        } finally {
                            // Interrupt current Thread since we can't continue indexing
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
    }
}
