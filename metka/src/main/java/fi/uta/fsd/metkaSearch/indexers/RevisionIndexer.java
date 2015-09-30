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
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.LuceneConfig;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.handlers.HandlerFactory;
import fi.uta.fsd.metkaSearch.handlers.RevisionHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.AlreadyClosedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RevisionIndexer extends Indexer {

    // Pool for indexer threads.
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final int MAX_KEY_QUE_FILLERS = 1;
    private static final int MAX_REVISION_COMMAND_HANDLERS = 1;
    private static final int MAX_REVISION_DATA_INDEXERS = 5;
    private static final int MAX_KEY_QUEUE_SIZE = 100;
    private static int ID = 1;

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

    @Override
    public void removeDocument(Term term) throws Exception {
        while(getStatus() == IndexerStatusMessage.FLUSHING) {
            Thread.sleep(50);
        }
        super.removeDocument(term);
        changeBatch++;
    }

    @Override
    public void removeDocument(Query query) throws Exception {
        while(getStatus() == IndexerStatusMessage.FLUSHING) {
            Thread.sleep(50);
        }
        super.removeDocument(query);
        changeBatch++;
    }

    @Override
    public void addDocument(Document document, Analyzer analyzer) throws Exception {
        while(getStatus() == IndexerStatusMessage.FLUSHING) {
            Thread.sleep(50);
        }
        super.addDocument(document, analyzer);
        changeBatch++;
    }

    public void updateDocument(Query removeQuery, Document document, Analyzer analyzer) throws Exception {
        while(getStatus() == IndexerStatusMessage.FLUSHING) {
            Thread.sleep(50);
        }
        super.removeDocument(removeQuery);
        super.addDocument(document, analyzer);
        changeBatch++;
    }

    private RevisionRepository revisions;
    private ConfigurationRepository configurations;
    private ReferenceService references;

    private BlockingQueue<RevisionKey> revisionKeyQueue = new ArrayBlockingQueue<>(MAX_KEY_QUEUE_SIZE);

    private RevisionIndexer(DirectoryManager manager, DirectoryManager.DirectoryPath path, IndexerCommandRepository commands,
                            RevisionRepository revisions, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        super(manager, path, commands);
        this.revisions = revisions;
        this.configurations = configurations;
        this.references = references;
    }

    @Override
    public IndexerStatusMessage call() throws Exception {
        List<Future<Boolean>> revisionKeyQueueFiller = new ArrayList<>();
        List<Future<Boolean>> commandHandlers = new ArrayList<>();
        List<Future<Boolean>> revisionHandlers = new ArrayList<>();

        boolean clearSkipped = false;

        Long flushTimer = System.currentTimeMillis();

        while(getStatus() != IndexerStatusMessage.STOP && getStatus() != IndexerStatusMessage.RETURNED) {
            try {
                while(revisionKeyQueueFiller.size() < MAX_KEY_QUE_FILLERS) {
                    revisionKeyQueueFiller.add(threadPool.submit(new RevisionKeyQueueFiller()));
                }

                while(commandHandlers.size() < MAX_REVISION_COMMAND_HANDLERS) {
                    commandHandlers.add(threadPool.submit(new CommandIndexer(this, ID++)));
                }
                while(revisionHandlers.size() < MAX_REVISION_DATA_INDEXERS) {
                    revisionHandlers.add(threadPool.submit(new RevisionDataIndexer(this, ID++)));
                }

                for(int i = 0; i < revisionKeyQueueFiller.size(); i++) {
                    if(revisionKeyQueueFiller.get(i).isDone()) {
                        revisionKeyQueueFiller.set(i, threadPool.submit(new RevisionKeyQueueFiller()));
                    }
                }
                for(int i = 0; i < commandHandlers.size(); i++) {
                    if(commandHandlers.get(i).isDone()) {
                        commandHandlers.set(i, threadPool.submit(new CommandIndexer(this, ID++)));
                    }
                }
                for(int i = 0; i < revisionHandlers.size(); i++) {
                    if(revisionHandlers.get(i).isDone()) {
                        revisionHandlers.set(i, threadPool.submit(new RevisionDataIndexer(this, ID++)));
                    }
                }

                long idleCheckTime = System.currentTimeMillis()-getIdleStart();
                if(changeBatch > 0 && ((revisionKeyQueue.isEmpty() && getStatus() == IndexerStatusMessage.IDLING && idleCheckTime >= LuceneConfig.TIME_IDLING_BEFORE_FLUSH)
                        || (LuceneConfig.FORCE_FLUSH_AFTER_BATCH_OF_CHANGES && changeBatch >= LuceneConfig.MAX_CHANGE_BATCH_SIZE))) {
                    Logger.info(getClass(), (!(revisionKeyQueue.isEmpty() && getStatus() == IndexerStatusMessage.IDLING && idleCheckTime >= LuceneConfig.TIME_IDLING_BEFORE_FLUSH)
                            ? "Forcing index flush." : "Flushing index after idle timer.")+" It's been "+(System.currentTimeMillis()-flushTimer)+"ms since last flush. PATH: "+getPath().toString());
                    IndexerStatusMessage status = getStatus();
                    setStatus(IndexerStatusMessage.FLUSHING);
                    Thread.sleep(500);
                    flushTimer = System.currentTimeMillis();
                    Logger.info(getClass(), "Flushing "+changeBatch+" index changes");
                    flushIndex();
                    changeBatch = 0;
                    setStatus(status);
                    clearSkipped = true;
                }
                if(clearSkipped && changeBatch == 0 && revisionKeyQueue.size() == 0) {
                    clearSkipped = false;
                    Pair<ReturnResult, Long> pair = revisions.getRevisionsWaitingIndexing();
                    if(pair.getRight() != null && pair.getRight() > 0) {
                        Logger.info(getClass(), "Clearing revisions that have been requested but not indexed since idle mode has been reached. PATH: "+getPath().toString());
                        revisions.clearPartlyIndexed();
                    }
                }

                // Check if process should continue running
                if(Thread.currentThread().isInterrupted()) {
                    cancelAll(revisionKeyQueueFiller);
                    cancelAll(commandHandlers);
                    cancelAll(revisionHandlers);

                    setStatus(IndexerStatusMessage.STOP);
                }

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                setStatus(IndexerStatusMessage.STOP);
                // Try to close the indexer
                Logger.warning(getClass(), "Closing index writer on path " + getPath().toString() + " because of interruption");
                indexer.getIndexWriter().close();
                //ex.printStackTrace();
                Thread.currentThread().interrupt();
                cancelAll(revisionKeyQueueFiller);
                cancelAll(commandHandlers);
                cancelAll(revisionHandlers);
            } catch(Exception e) {
                Logger.error(getClass(), "Exception while indexing", e);
                Thread.currentThread().interrupt();
                cancelAll(revisionKeyQueueFiller);
                cancelAll(commandHandlers);
                cancelAll(revisionHandlers);
            }
        }
        if(getStatus() == IndexerStatusMessage.STOP) {
            indexer.getIndexWriter().close();
            cancelAll(revisionKeyQueueFiller);
            cancelAll(commandHandlers);
            cancelAll(revisionHandlers);
        }
        return IndexerStatusMessage.RETURNED;
    }

    private void cancelAll(List<Future<Boolean>> handlers) {
        for(Future<Boolean> handler : handlers) {
            if(!handler.isDone()) {
                handler.cancel(true);
            }
        }
    }


    synchronized private IndexerCommand getNextCommand() {
        return commands.getNextCommand(getPath().getType(), getPath().toString());
    }
    private void clearIndexing(RevisionKey key) {
        revisions.clearIndexing(key);
    }
    private void markAsIndexed(RevisionKey key) {
        revisions.markAsIndexed(key);
    }

    private class RevisionKeyQueueFiller implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            while(!Thread.currentThread().isInterrupted() && getStatus() != IndexerStatusMessage.STOP) {
                try {
                    RevisionKey key = getNextKey();
                    if(!revisionKeyQueue.offer(key)) {
                        clearIndexing(key);
                        Thread.sleep(2000);
                    }
                } catch(InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    setStatus(IndexerStatusMessage.STOP);
                }
            }
            return true;
        }

        private RevisionKey getNextKey() {
            return revisions.getNextForIndexing();
        }
    }

    private class CommandIndexer implements Callable<Boolean> {
        private final RevisionHandler handler;
        private final int ID;

        public CommandIndexer(RevisionIndexer indexer, int ID) {
            this.handler = HandlerFactory.buildRevisionHandler(indexer, revisions, configurations, references);
            this.ID = ID;
        }

        @Override
        public Boolean call() throws Exception {
            long timeHandlingCommands = 0L;
            long previousTime;
            long total = 0L;
            boolean firstBatch = true;
            boolean first100Batch = true;
            long batchIn10Minutes = 0L;
            long extraTime = 0L;
            long handleEnd = 0L;
            boolean firstIdle = true;
            while(!Thread.currentThread().isInterrupted() && getStatus() != IndexerStatusMessage.STOP) {
                try {
                    while(getStatus() == IndexerStatusMessage.FLUSHING) {
                        Thread.sleep(100);
                    }
                    IndexerCommand command = getNextCommand();
                    if(command != null) {
                        if(getStatus() != IndexerStatusMessage.STOP && getStatus() != IndexerStatusMessage.FLUSHING) {
                            setStatus(IndexerStatusMessage.PROCESSING);
                        } else {
                            commands.clearCommandRequest(command.getQueueId());
                            continue;
                        }
                        long start = System.currentTimeMillis();
                        if(handleEnd > 0L) extraTime += start-handleEnd;
                        Logger.debug(getClass(), "Started new command for: " + command.getPath());
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
                                    query.add(NumericRangeQuery.newLongRange("key.no", 1, rCom.getNo().longValue(), rCom.getNo().longValue(), true, true), BooleanClause.Occur.MUST);
                                    try {
                                        removeDocument(query);
                                        commandHandled = true;
                                    } catch(AlreadyClosedException ace) {
                                        setStatus(IndexerStatusMessage.STOP);
                                        commands.clearCommandRequest(command.getQueueId());
                                        continue;
                                    }
                                    break;
                                case INDEX:
                                    try {
                                        Logger.debug(getClass(), "Performing INDEX action on revision");
                                        commandHandled = indexCommand(rCom);
                                    } catch(AlreadyClosedException ace) {
                                        setStatus(IndexerStatusMessage.STOP);
                                        commands.clearCommandRequest(command.getQueueId());
                                        continue;
                                    }
                                    break;
                                case STOP:
                                    // This is here to remove compiler warning, actual STOP command is handled earlier
                                    commandHandled = true;
                                    break;
                            }
                        }
                        // Assume that command was handled appropriately
                        if(commandHandled) {
                            commands.markCommandAsHandled(command.getQueueId());
                        } else {
                            commands.clearCommandRequest(command.getQueueId());
                            continue;
                        }


                        // DEBUG
                        long end = System.currentTimeMillis();
                        handleEnd = end;
                        previousTime = timeHandlingCommands;
                        timeHandlingCommands += end-start;

                        total++;
                        batchIn10Minutes++;
                        if(total%100 == 0 && first100Batch) {
                            first100Batch = false;
                            Logger.info(getClass(), "Took "+(timeHandlingCommands+extraTime)+ "ms to handle first 100 commands. "
                                    + "ID: " + ID + ". "
                                    + "PATH: " + getPath().toString());
                        }

                        if(checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60)) {
                            if(firstBatch) {
                                firstBatch = false;
                                Logger.info(getClass(), "Handled "+batchIn10Minutes+" commands in first minute of current batch. "
                                        + "ID: " + ID + ". "
                                        + "PATH: " + getPath().toString());
                            }
                        }
                        if(checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60 * 10)) {
                            Logger.info(getClass(), "Handled "+batchIn10Minutes+" commands in previous 10 minutes of current batch. "
                                    + "ID: " + ID + ". "
                                    + "PATH: " + getPath().toString());
                            batchIn10Minutes = 0L;
                        }
                    } else {
                        if(getStatus() != IndexerStatusMessage.IDLING) {
                            setStatus(IndexerStatusMessage.IDLING);
                        }
                        if(firstIdle) {
                            // Previous loop was handling command, post DEBUG info
                            if(total > 0) {
                                Logger.info(getClass(), "Queue clear. Spent " + timeHandlingCommands + "ms handling " + total + " commands + " + extraTime + "ms of extra time. "
                                        + "ID: " + ID + ". "
                                        + "PATH: " + getPath().toString());
                            }
                            timeHandlingCommands = 0L;
                            extraTime = 0L;
                            handleEnd = 0L;
                            total= 0L;
                            firstBatch = true;
                            first100Batch = true;
                            batchIn10Minutes = 0L;
                            firstIdle = false;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    // Try to close the indexer
                    Logger.warning(getClass(), "Closing index handler on indexer " + getPath().toString() + " because of interruption");
                    //ex.printStackTrace();
                    Thread.currentThread().interrupt();
                    setStatus(IndexerStatusMessage.STOP);
                } catch(Exception e) {
                    Logger.error(getClass(), "Exception while indexing", e);
                    Thread.currentThread().interrupt();
                }
            }

            return true;
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

    private class RevisionDataIndexer implements Callable<Boolean> {
        private final RevisionHandler handler;
        private final int ID;

        public RevisionDataIndexer(RevisionIndexer indexer, int ID) {
            this.handler = HandlerFactory.buildRevisionHandler(indexer, revisions, configurations, references);
            this.ID = ID;
        }

        @Override
        public Boolean call() throws Exception {
            // Debug guides
            boolean firstBatch = true;
            boolean first100Batch = true;

            long timeHandlingCommands = 0L;
            long previousTime;
            long total = 0L;

            long batchIn10Minutes = 0L;
            long extraTime = 0L;
            long handleEnd = 0L;
            boolean firstIdle = true;
            while(!Thread.currentThread().isInterrupted() && getStatus() != IndexerStatusMessage.STOP) {
                try {
                    while(getStatus() == IndexerStatusMessage.FLUSHING) {
                        Thread.sleep(100);
                    }
                    RevisionKey key = revisionKeyQueue.poll();
                    if(key != null) {
                        if(getStatus() != IndexerStatusMessage.STOP && getStatus() != IndexerStatusMessage.FLUSHING) {
                            setStatus(IndexerStatusMessage.PROCESSING);
                        } else {
                            clearIndexing(key);
                            continue;
                        }
                        long start = System.currentTimeMillis();
                        if(handleEnd > 0L) extraTime += start-handleEnd;
                        Logger.debug(getClass(), "Started new indexing for: " + key);
                        firstIdle = true;

                        // Index revision
                        try {
                            if(indexRevision(key)) {
                                markAsIndexed(key);
                            } else {
                                clearIndexing(key);
                                continue;
                            }
                        } catch(AlreadyClosedException ace) {
                            clearIndexing(key);
                            setStatus(IndexerStatusMessage.STOP);
                            continue;
                        }

                        // DEBUG
                        long end = System.currentTimeMillis();
                        handleEnd = end;
                        previousTime = timeHandlingCommands;
                        timeHandlingCommands += end-start;

                        total++;
                        batchIn10Minutes++;
                        if(total%100 == 0 && first100Batch) {
                            first100Batch = false;
                            Logger.info(getClass(), "Took "+ (timeHandlingCommands + extraTime) + "ms to handle first 100 commands and revisions. "
                                    + "ID: " + ID + ". "
                                    + "PATH: " + getPath().toString());
                        }

                        if(checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60)) {
                            if(firstBatch) {
                                firstBatch = false;
                                Logger.info(getClass(), "Indexed "+batchIn10Minutes+" in first minute of current batch. "
                                        + "ID: " + ID + ". "
                                        + "PATH: " + getPath().toString());
                            }
                        }
                        if(checkInterval(timeHandlingCommands, previousTime, extraTime, 1000 * 60 * 10)) {
                            Logger.info(getClass(), "Indexed "+batchIn10Minutes+" in previous 10 minutes of current batch. "
                                    + "ID: " + ID + ". "
                                    + "PATH: " + getPath().toString());
                            batchIn10Minutes = 0L;
                        }
                        Logger.debug(getClass(), "Took " + (end - start) + "ms to index revision. PATH: "+getPath().toString());
                    } else {
                        if(getStatus() != IndexerStatusMessage.IDLING) {
                            setStatus(IndexerStatusMessage.IDLING);
                        }
                        if(firstIdle) {
                            // Previous loop was handling command, post DEBUG info
                            if(total > 0) {
                                Logger.info(getClass(), "Queue clear. Spent " + timeHandlingCommands + "ms indexing "+total+" revisions + "+extraTime+"ms of extra time. "
                                        + "ID: " + ID + ". "
                                        + "PATH: " + getPath().toString());
                            }
                            timeHandlingCommands = 0L;
                            extraTime = 0L;
                            handleEnd = 0L;
                            total = 0L;
                            firstBatch = true;
                            first100Batch = true;
                            batchIn10Minutes = 0L;
                            firstIdle = false;
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    // Try to close the indexer
                    Logger.warning(getClass(), "Closing index handler on indexer " + getPath().toString() + " because of interruption");
                    Thread.currentThread().interrupt();
                    //ex.printStackTrace();
                    setStatus(IndexerStatusMessage.STOP);
                } catch(Exception e) {
                    Logger.error(getClass(), "Exception while indexing", e);
                    Thread.currentThread().interrupt();
                }
            }

            return true;
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
