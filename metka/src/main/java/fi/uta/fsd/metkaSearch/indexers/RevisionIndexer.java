/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.handlers.HandlerFactory;
import fi.uta.fsd.metkaSearch.handlers.RevisionHandler;
import org.apache.lucene.search.*;
import org.apache.lucene.store.AlreadyClosedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RevisionIndexer extends Indexer {

    // Pool for indexer threads.
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private static int MAX_REVISION_DATA_INDEXERS = 3;

    public static RevisionIndexer build(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                                        RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        if(manager == null) {
            throw new UnsupportedOperationException("Needs a directory manager");
        }
        // Check that additional parameters matches requirements
        if(path.getAdditionalParameters().length > 0) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too many additional parameters");
        }
        if(revisions == null || configurations == null) {
            throw new UnsupportedOperationException("Revision indexer needs access to revision and configuration repositories");
        }

        return new RevisionIndexer(manager, path, commands, revisions, configurations, references);
    }

    // Counter for idle loops. If there's been changes to index they will be flushed after certain number of times.
    volatile private int changeBatch = 0;
    // Switch to see if there has been indexed data that needs to be flushed to disk
    volatile private boolean indexChanged = false;

    private RevisionRepository revisions;
    private ConfigurationRepository configurations;
    private ReferenceService references;

    private RevisionIndexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                            RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        super(manager, path, commands);
        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
    }

    @Override
    public IndexerStatusMessage call() throws Exception {
        Future<IndexerStatusMessage> commandHandler = null;
        List<Future<IndexerStatusMessage>> revisionHandlers = new ArrayList<>();
        while(getStatus() != IndexerStatusMessage.STOP && getStatus() != IndexerStatusMessage.RETURNED) {
            try {
                if(commandHandler == null) {
                    commandHandler = threadPool.submit(new CommandIndexer(this, revisions, configurations, references, commands));
                }
                while(revisionHandlers.size() < MAX_REVISION_DATA_INDEXERS) {
                    revisionHandlers.add(threadPool.submit(new RevisionDataIndexer(this, revisions, configurations, references)));
                }

                if(commandHandler.isDone()) {
                    commandHandler = threadPool.submit(new CommandIndexer(this, revisions, configurations, references, commands));
                }

                for(int i = 0; i < revisionHandlers.size(); i++) {
                    if(revisionHandlers.get(i).isDone()) {
                        revisionHandlers.set(i, threadPool.submit(new RevisionDataIndexer(this, revisions, configurations, references)));
                    }
                }

                if(indexChanged && ((getStatus() == IndexerStatusMessage.IDLING && System.currentTimeMillis()-getIdleStart() >= LuceneConfig.TIME_IDLING_BEFORE_FLUSH)
                        || (LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_CHANGES && changeBatch >= LuceneConfig.MAX_CHANGE_BATCH_SIZE))) {
                    flushIndex();
                    indexChanged = false;
                }

                // Check if process should continue running
                if(Thread.currentThread().isInterrupted()) {
                    if(!commandHandler.isDone()) {
                        commandHandler.cancel(true);
                    }
                    for(int i = 0; i < revisionHandlers.size(); i++) {
                        if(!revisionHandlers.get(i).isDone()) {
                            revisionHandlers.get(i).cancel(true);
                        }
                    }

                    setStatus(IndexerStatusMessage.STOP);
                }
                // If there was no new commands to handle, idle for 5 seconds before checking again
                // Otherwise continue straight to next command
                if(getStatus() == IndexerStatusMessage.IDLING) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                setStatus(IndexerStatusMessage.STOP);
                // Try to close the indexer
                Logger.warning(getClass(), "Closing index writer on path " + getPath().toString() + " because of interruption");
                indexer.getIndexWriter().close();
                //ex.printStackTrace();
                Thread.currentThread().interrupt();
            } catch(Exception e) {
                Logger.error(getClass(), "Exception while indexing", e);
                Thread.currentThread().interrupt();
            }
        }
        if(getStatus() == IndexerStatusMessage.STOP) {
            indexer.getIndexWriter().close();
            if(!commandHandler.isDone()) {
                commandHandler.cancel(true);
            }
            for(int i = 0; i < revisionHandlers.size(); i++) {
                if(!revisionHandlers.get(i).isDone()) {
                    revisionHandlers.get(i).cancel(true);
                }
            }
        }
        return IndexerStatusMessage.RETURNED;
    }

    synchronized private RevisionKey getNextKey() {
        return revisions.getNextForIndexing();
    }

    synchronized private IndexerCommand getNextCommand() {
        return commands.getNextCommand(getPath().getType(), getPath().toString());
    }

    private class CommandIndexer implements Callable<IndexerStatusMessage> {
        private final RevisionIndexer indexer;
        private final RevisionHandler handler;
        private final IndexerCommandRepository commands;

        public CommandIndexer(RevisionIndexer indexer, RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references, IndexerCommandRepository commands) {
            this.indexer = indexer;
            this.commands = commands;
            this.handler = HandlerFactory.buildRevisionHandler(this.indexer, revisions, configurations, references);
        }

        @Override
        public IndexerStatusMessage call() throws Exception {
            long timeHandlingCommands = 0L;
            long previousTime = 0L;
            long batch = 0L;
            boolean firstBatch = true;
            boolean first100Batch = true;
            long batchIn10Minutes = 0L;
            long extraTime = 0L;
            long handleEnd = 0L;
            boolean firstIdle = true;
            while(!Thread.currentThread().isInterrupted() && getStatus() != IndexerStatusMessage.STOP) {
                try {
                    IndexerCommand command = indexer.getNextCommand();
                    if(command != null) {
                        long start = System.currentTimeMillis();
                        if(handleEnd > 0L) extraTime += start-handleEnd;
                        Logger.debug(getClass(), "Started new command for: " + command.getPath());
                        if(getStatus() != IndexerStatusMessage.STOP) {
                            setStatus(IndexerStatusMessage.PROCESSING);
                        } else {
                            commands.clearCommandRequest(command.getQueueId());
                            continue;
                        }
                        firstIdle = true;

                        boolean commandHandled = false;

                        // Handle command
                        if(command.getAction() == IndexerCommand.Action.STOP) {
                            // Indexer is requested to stop
                            setStatus(IndexerStatusMessage.STOP);
                            commandHandled = true;
                        } else {
                            // Forward handling to implementation
                            Logger.debug(getClass(), "Starting handling or revision command");
                            // This is a safe type conversion since Indexers add command only accepts commands of correct type
                            RevisionIndexerCommand rCom = (RevisionIndexerCommand) command;

                            switch(rCom.getAction()) {
                                case REMOVE:
                                    Logger.debug(getClass(), "Performing REMOVE action on revision");
                                    // Create term for identification
                                    if(rCom.getId() == null || rCom.getNo() == null) {
                                        break;
                                    }
                                    BooleanQuery query = new BooleanQuery();
                                    query.add(NumericRangeQuery.newLongRange("key.id", 1, rCom.getId(), rCom.getId(), true, true), BooleanClause.Occur.MUST);
                                    query.add(NumericRangeQuery.newIntRange("key.no", 1, rCom.getNo(), rCom.getNo(), true, true), BooleanClause.Occur.MUST);
                                    try {
                                        this.indexer.removeDocument(query);
                                        commandHandled = true;
                                    } catch(AlreadyClosedException ace) {
                                        setStatus(IndexerStatusMessage.STOP);
                                        commands.clearCommandRequest(command.getQueueId());
                                        continue;
                                    } catch(Exception e) {
                                        Logger.error(getClass(), "Exception while removing revision ["+rCom.getId()+","+rCom.getNo()+"] from index.", e);
                                    }
                                    break;
                                case INDEX:
                                    Logger.debug(getClass(), "Performing INDEX action on revision");
                                    commandHandled = indexCommand(rCom);
                                    break;
                                case STOP:
                                    // This is here to remove compiler warning, actual STOP command is handled earlier
                                    commandHandled = true;
                                    break;
                            }
                            // Set indexChanged to true since command was handled
                            indexChanged = true;
                        }
                        // Assume that command was handled appropriately
                        if(commandHandled) {
                            commands.markCommandAsHandled(command.getQueueId());
                            if(LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_CHANGES) {
                                changeBatch++;
                            }
                        } else {
                            commands.clearCommandRequest(command.getQueueId());
                            continue;
                        }


                        // DEBUG
                        long end = System.currentTimeMillis();
                        handleEnd = end;
                        previousTime = timeHandlingCommands;
                        timeHandlingCommands += end-start;

                        batch++;
                        batchIn10Minutes++;
                        if(batch%100 == 0 && first100Batch) {
                            first100Batch = false;
                            Logger.info(getClass(), "Took "+(timeHandlingCommands+extraTime)+ "ms to handle first 100 commands. PATH: "+getPath().toString());
                        }

                        if(indexer.checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60)) {
                            if(firstBatch) {
                                firstBatch = false;
                                Logger.info(getClass(), "Handled "+batchIn10Minutes+" commands in first minute of current batch. PATH: "+getPath().toString());
                            }
                        }
                        if(indexer.checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60 * 10)) {
                            Logger.info(getClass(), "Handled "+batchIn10Minutes+" commands in previous 10 minutes of current batch. PATH: "+getPath().toString());
                            batchIn10Minutes = 0L;
                        }
                        //Logger.debug(getClass(), "Took " + (end - start) + "ms to handle command. PATH: "+getPath().toString());
                    } else {
                        if(getStatus() != IndexerStatusMessage.IDLING) {
                            // Previous loop was handling command, post DEBUG info
                            setStatus(IndexerStatusMessage.IDLING);
                        }
                        if(firstIdle) {
                            Logger.info(getClass(), "Queue clear. Spent " + timeHandlingCommands + "ms handling "+batch+" commands + "+extraTime+"ms of extra time. PATH: "+getPath().toString());
                            timeHandlingCommands = 0L;
                            extraTime = 0L;
                            handleEnd = 0L;
                            batch= 0L;
                            firstBatch = true;
                            first100Batch = true;
                            previousTime = 0L;
                            batchIn10Minutes = 0L;
                            firstIdle = false;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    // Try to close the indexer
                    Logger.warning(getClass(), "Closing index handler on indexer " + this.indexer.getPath().toString() + " because of interruption");
                    //ex.printStackTrace();
                    Thread.currentThread().interrupt();
                    setStatus(IndexerStatusMessage.STOP);
                } catch(Exception e) {
                    Logger.error(getClass(), "Exception while indexing", e);
                    Thread.currentThread().interrupt();
                }
            }

            return IndexerStatusMessage.RETURNED;
        }



        /**
         * Create Document out of a revision and add it to the writer.
         *
         * @param command
         */
        private boolean indexCommand(RevisionIndexerCommand command) throws AlreadyClosedException {
            Logger.debug(getClass(), "Trying to handle revision command");
            return handler.handle(command);
        }
    }

    private class RevisionDataIndexer implements Callable<IndexerStatusMessage> {
        private final RevisionIndexer indexer;
        private final RevisionHandler handler;
        private final RevisionRepository revisions;

        public RevisionDataIndexer(RevisionIndexer indexer, RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references) {
            this.indexer = indexer;
            this.revisions = revisions;
            this.handler = HandlerFactory.buildRevisionHandler(this.indexer, revisions, configurations, references);
        }

        @Override
        public IndexerStatusMessage call() throws Exception {
            long timeHandlingCommands = 0L;
            long previousTime = 0L;
            long batch = 0L;
            boolean firstBatch = true;
            boolean first100Batch = true;
            long batchIn10Minutes = 0L;
            long extraTime = 0L;
            long handleEnd = 0L;
            boolean firstIdle = true;
            while(!Thread.currentThread().isInterrupted() && getStatus() != IndexerStatusMessage.STOP) {
                try {
                    RevisionKey key = indexer.getNextKey();
                    if(key != null) {
                        long start = System.currentTimeMillis();
                        if(handleEnd > 0L) extraTime += start-handleEnd;
                        Logger.debug(getClass(), "Started new indexing for: " + key);
                        if(getStatus() != IndexerStatusMessage.STOP) {
                            setStatus(IndexerStatusMessage.PROCESSING);
                        } else {
                            revisions.clearIndexing(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
                            continue;
                        }
                        firstIdle = true;

                        // Index revision
                        // Set indexChanged to true since command was handled

                        // Assume that command was handled appropriately
                        try {
                            if(indexRevision(key)) {
                                indexChanged = true;
                                revisions.markAsIndexed(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
                                if(LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_CHANGES) {
                                    changeBatch++;
                                }
                            } else {
                                revisions.clearIndexing(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
                                continue;
                            }
                        } catch(AlreadyClosedException ace) {
                            revisions.clearIndexing(fi.uta.fsd.metka.storage.entity.key.RevisionKey.fromModelKey(key));
                            setStatus(IndexerStatusMessage.STOP);
                            continue;
                        }

                        // DEBUG
                        long end = System.currentTimeMillis();
                        handleEnd = end;
                        previousTime = timeHandlingCommands;
                        timeHandlingCommands += end-start;

                        batch++;
                        batchIn10Minutes++;
                        if(batch%100 == 0 && first100Batch) {
                            first100Batch = false;
                            Logger.info(getClass(), "Took "+ (timeHandlingCommands + extraTime) + "ms to handle first 100 commands and revisions. PATH: "+getPath().toString());
                        }

                        if(indexer.checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60)) {
                            if(firstBatch) {
                                firstBatch = false;
                                Logger.info(getClass(), "Indexed "+batchIn10Minutes+" in first minute of current batch. PATH: "+getPath().toString());
                            }
                        }
                        if(indexer.checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60 * 10)) {
                            Logger.info(getClass(), "Indexed "+batchIn10Minutes+" in previous 10 minutes of current batch. PATH: "+getPath().toString());
                            batchIn10Minutes = 0L;
                        }
                        Logger.debug(getClass(), "Took " + (end - start) + "ms to index revision. PATH: "+getPath().toString());
                    } else {
                        if(getStatus() != IndexerStatusMessage.IDLING) {
                            // Previous loop was handling command, post DEBUG info
                            setStatus(IndexerStatusMessage.IDLING);
                        }
                        if(firstIdle) {
                            Logger.info(getClass(), "Queue clear. Spent " + timeHandlingCommands + "ms indexing "+batch+" revisions + "+extraTime+"ms of extra time. PATH: "+getPath().toString());
                            timeHandlingCommands = 0L;
                            extraTime = 0L;
                            handleEnd = 0L;
                            batch = 0L;
                            firstBatch = true;
                            first100Batch = true;
                            previousTime = 0L;
                            batchIn10Minutes = 0L;
                            firstIdle = false;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    // Try to close the indexer
                    Logger.warning(getClass(), "Closing index handler on indexer " + this.indexer.getPath().toString() + " because of interruption");
                    Thread.currentThread().interrupt();
                    //ex.printStackTrace();
                    setStatus(IndexerStatusMessage.STOP);
                } catch(Exception e) {
                    Logger.error(getClass(), "Exception while indexing", e);
                    Thread.currentThread().interrupt();
                }
            }

            return IndexerStatusMessage.RETURNED;
        }

        /**
         * Create Document out of a revision and add it to the writer.
         *
         * @param key
         */
        private boolean indexRevision(RevisionKey key) throws AlreadyClosedException{
            Logger.debug(getClass(), "Trying to handle revision command");
            return handler.handle(key);
        }
    }
}
