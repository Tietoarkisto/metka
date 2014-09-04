package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Base class for all Indexers in the software.
 * Implements basic functionality common for all indexers but all specialized stuff needs to be handled elsewhere.
 */
public abstract class Indexer implements Callable<IndexerStatusMessage>/*, IndexerCommandHandler*/ {
    private static Logger logger = LoggerFactory.getLogger(Indexer.class);

    protected static void checkPathType(DirectoryManager.DirectoryPath path, IndexerConfigurationType type) throws UnsupportedOperationException {
        if(path.getType() != type) {
            throw new UnsupportedOperationException("Path is for a different type");
        }
    }

    private IndexerStatusMessage status;

    // INFORMATION FOR INDEX HANDLING
    // Switch to see if there has been indexed data that needs to be flushed to disk
    private boolean indexChanged = false;
    // Counter for idle loops. If there's been changes to index they will be flushed after certain number of times.
    private int idleLoops = 0;
    // Counter for processed commands. Used to detect when to force flush the index if that setting is true.
    private int commandBatch = 0;

    private final DirectoryInformation indexer;

    private final IndexerCommandRepository commands;

    private final IndexWriter indexWriter;

    private final DirectoryManager.DirectoryPath path;

    //protected BlockingQueue<IndexerCommand> commandQueue = new LinkedBlockingQueue<>();

    protected Indexer(DirectoryManager.DirectoryPath path, IndexerCommandRepository commands) throws UnsupportedOperationException {
        this.path = path;
        indexer = DirectoryManager.getIndexDirectory(path, true);
        if(indexer == null) {
            throw new UnsupportedOperationException("Couldn't get an index directory for indexer with path "+path);
        }
        indexWriter = indexer.getIndexWriter();
        if(indexWriter == null) {
            throw new UnsupportedOperationException("Can't create Indexer, no indexWriter created for Indexer with path "+path);
        }
        this.commands = commands;
    }

    protected void setStatus(IndexerStatusMessage status) {
        this.status = status;
    }

    protected IndexerStatusMessage getStatus() {
        return status;
    }

    protected DirectoryInformation getIndexer() {
        return indexer;
    }

    public DirectoryManager.DirectoryPath getPath() {
        return path;
    }

    @Override
    public IndexerStatusMessage call() throws Exception {

        // DEBUG
        long timeHandlingCommands = 0L;

        while(status != IndexerStatusMessage.STOP && status != IndexerStatusMessage.RETURNED) {
            IndexerCommand command = null;
            try {
                //IndexerCommand command = commandQueue.poll(5, TimeUnit.SECONDS);
                command = commands.getNextCommand(indexer.getPath().getType(), indexer.getPath().getLanguage());
                if(command != null) {
                    long start = System.currentTimeMillis();
                    logger.info("Started new command for: "+command.getPath());
                    status = IndexerStatusMessage.PROCESSING;

                    // Reset idle loops since there's work
                    idleLoops = 0;

                    // Handle command
                    if(command.getAction() == IndexerCommand.Action.STOP) {
                        // Indexer is requested to stop
                        setStatus(IndexerStatusMessage.STOP);
                    } else {
                        // Forward handling to implementation
                        logger.info("Trying to handle command.");
                        handleCommand(command);
                        // Set indexChanged to true since command was handled
                        indexChanged = true;
                    }
                    // Assume that command was handled appropriately
                    commands.markCommandAsHandled(command.getQueueId());

                    if(LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS) {
                        commandBatch++;
                    }
                    // DEBUG
                    long end = System.currentTimeMillis();
                    timeHandlingCommands += end-start;
                    logger.info("Took "+(end-start)+"ms to handle command");
                } else {
                    if(status != IndexerStatusMessage.IDLING) {
                        idleLoops = 0;
                        // Previous loop was handling command, post DEBUG info
                        logger.info("Queue clear. Spent "+timeHandlingCommands+"ms handling commands");
                        status = IndexerStatusMessage.IDLING;
                        timeHandlingCommands = 0L;
                    }
                    if(indexChanged) idleLoops++;
                }

                if(idleLoops > 1 && status != IndexerStatusMessage.IDLING) {
                    // State that the handler is idling only after at least one idle loop has been passed
                    // It's safer to assume that we are actually idle if we have been idle for at least a loop
                    status = IndexerStatusMessage.IDLING;
                }

                if(indexChanged && (idleLoops >= LuceneConfig.IDLE_LOOPS_BEFORE_FLUSH
                        || (LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_COMMANDS && commandBatch >= LuceneConfig.MAX_COMMAND_BATCH_SIZE))) {
                    flushIndex();
                }

                // Check if process should continue running
                if(Thread.currentThread().isInterrupted()) {
                    status = IndexerStatusMessage.STOP;
                }
                // If there was no new commands to handle, idle for 5 seconds before checking again
                // Otherwise continue straight to next command
                if(status == IndexerStatusMessage.IDLING) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {
                // Try to close the indexer
                logger.info("Closing index writer on path "+getPath().toString()+" because of interruption");
                indexer.getIndexWriter().close();
                //ex.printStackTrace();
                throw new InterruptedException();
            } catch(Exception e) {
                if(command != null) {
                    logger.error("Exception while handling command: "+"("+command.getQueueId()+") "+command.getPath()+"/"+command.getAction());
                }
                Thread.currentThread().interrupt();
            }
        }
        if(status == IndexerStatusMessage.STOP) {
            indexer.getIndexWriter().close();
        }
        status = IndexerStatusMessage.RETURNED;
        return IndexerStatusMessage.RETURNED;
    }

    protected abstract void handleCommand(IndexerCommand command);

    public void removeDocument(Term term) {
        // TODO: OutOfMemory checks and writer closing
        try {
            indexWriter.deleteDocuments(term);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while trying to delete documents with term " + term.toString());
        }
    }

    public void removeDocument(Query query) {
        try {
            indexWriter.deleteDocuments(query);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while trying to delete documents with query " + query.toString());
        }
    }

    public void addDocument(Document document, Analyzer analyzer) {
        try {
            indexWriter.addDocument(document, analyzer);
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while trying to add document to indexer with path "+path);
        }
    }

    private void flushIndex() {
        logger.info("Preparing to flush index "+indexer.getPath().toString());
        indexChanged = false;
        idleLoops = 0;
        commandBatch = 0;
        try {
            // Try to commit the writer
            logger.info("Trying index writer commit.");
            indexWriter.commit();

            // Set indexer to dirty state so that searchers know to update their index
            // Not needed anymore sine searches always reopen the index anyway
            //indexer.setDirty(true);
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
            // If we get an OutOfMemoryError then close the writer immediately
            try {
                // Try closing the writer
                indexWriter.close();
            } catch(OutOfMemoryError erc) {
                // As I understand it we should get another OutOfMemoryError, close the writer again
                try {
                    // Try closing the writer
                    indexWriter.close();
                } catch(IOException ioe) {
                    ioe.printStackTrace();
                    logger.error("IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
                }
            } catch(IOException ioe) {
                ioe.printStackTrace();
                logger.error("IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
            } finally {
                // Interrupt current Thread since we can't continue indexing
                Thread.currentThread().interrupt();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            logger.error("IOException while trying to flush indexWriter for indexer in path "+indexer.getPath());
        }
    }
}
