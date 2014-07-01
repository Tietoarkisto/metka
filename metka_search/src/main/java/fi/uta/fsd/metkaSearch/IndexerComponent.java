package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.WikipediaIndexerCommand;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.information.DirectoryInformation;
import fi.uta.fsd.metkaSearch.sax.WikipediaHandler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileReader;
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
    public static boolean handlingCommand = false;

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
        boolean done = commandHandler.isDone();
        boolean cancelled = commandHandler.isCancelled();
        return !done;
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
                //indexer = LuceneFactory.getInMemoryIndexWriter();
                indexer = LuceneFactory.getIndexWriter(IndexerConfigurationType.TEST);

                while(running) {
                    try {
                        handlingCommand = false;
                        IndexerCommand command = commandQueue.poll(5, TimeUnit.SECONDS);
                        if(command != null) {
                            handlingCommand = true;
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
                            if(indexChanged) {
                                idleLoops++;
                                System.err.println("Number of idle loops: "+idleLoops);
                            }
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

            private void handleCommand(IndexerCommand command) throws IOException, SAXException {
                System.err.println("New "+command.getType()+" Command with action: "+command.getAction());
                switch(command.getType()) {
                    case WIKIPEDIA:
                        handleWikipediaCommand((WikipediaIndexerCommand)command);
                        break;
                }
            }

            private void handleWikipediaCommand(WikipediaIndexerCommand command) throws IOException, SAXException {
                switch(command.getAction()) {
                    case REMOVE:
                        // Create term for identification
                        if(StringUtils.isEmpty(command.getPageId())) {
                            break;
                        }
                        Term term = new Term("id", command.getPageId());
                        removeDocument(term);
                        break;
                    case INDEX:
                        indexWikipediaCommand(command);
                        break;
                }
            }

            /**
             * Create Document out of an XML file and add it to the writer.
             *
             * @param command
             */
            private void indexWikipediaCommand(WikipediaIndexerCommand command) throws IOException, SAXException {
                if(StringUtils.isEmpty(command.getPath())) {
                    // No sense in trying to parse empty path
                    return;
                }
                // Create sax parser
                XMLReader xr;
                try {
                    xr = XMLReaderFactory.createXMLReader();
                } catch(SAXException sex) {
                    sex.printStackTrace();
                    return;
                }
                // Create handler
                WikipediaHandler handler = new WikipediaHandler(indexer, command);
                xr.setContentHandler(handler);
                xr.setErrorHandler(handler);

                // Try parsing the file
                FileReader fr = new FileReader(command.getPath());
                xr.parse(new InputSource(fr));

                // Get the XML-file and parse it to some format for processing

                // Add fields from the XML-file to index sometimes including a different analyzer

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
                        // Set indexer to dirty state so that searchers know to update their index
                        indexer.setDirty(true);
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
