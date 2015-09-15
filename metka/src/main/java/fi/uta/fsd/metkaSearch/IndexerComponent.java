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

package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.RevisionIndexer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Service
public class IndexerComponent {

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private IndexerCommandRepository commandRepository;

    @Autowired
    private ReferenceService references;

    @Autowired
    private DirectoryManager manager;

    // Pool for indexer threads.
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Map of indexers.
     * Each indexer handles a single index based on its IndexerConfigurationType
     * and takes care of commands linked to that index.
     * All objects within a single index should be parsed for indexing using similar procedures.
     * All objects within a single index should also be searchable using similar procedures.
     */
    private final Map<IndexerConfigurationType, Future<IndexerStatusMessage>> handlers = new ConcurrentHashMap<>();

    /**
     * Executed at program startup. Clears requested status from all non handled methods and removes all handled methods from database.
     */
    @PostConstruct
    public void clearCommands() {
        commandRepository.clearAllRequests();
        commandRepository.removeAllHandled();

        // Clear lock
        DirectoryManager.DirectoryPath path = DirectoryManager
                .formPath(false, IndexerConfigurationType.REVISION);
        try {
            DirectoryInformation info = manager.getIndexDirectory(path, false);
            Logger.debug(getClass(), "Checking directory " + path + " for write lock.");
            if(IndexWriter.isLocked(info.getDirectory())) {
                Logger.debug(getClass(), "Directory "+path+" contained lock. Attempting to clear lock with name "+IndexWriter.WRITE_LOCK_NAME+" from directory.");
                IndexWriter.unlock(info.getDirectory());
                info.getDirectory().clearLock(IndexWriter.WRITE_LOCK_NAME);
                if(IndexWriter.isLocked(info.getDirectory())) {
                    Logger.debug(getClass(), "FAIL during lock clearing for path " + path + " attempting forced delete since we know that the lock should not be in use");
                    info.getDirectory().deleteFile(IndexWriter.WRITE_LOCK_NAME);
                } else {
                    Logger.debug(getClass(), "SUCCESS during lock clearing for path " + path);
                }
            }
        } catch(Exception e) {
            Logger.error(getClass(), "Exception while clearing path " + path + " from write lock:", e);
        }
    }

    /**
     * Checks for commands that have handlers that have stopped for one reason or another.
     */
    @Scheduled(fixedDelay = 5000)
    public void checkIndexers() {
        /*IndexerCommand command = commandRepository.getNextCommandWithoutChange();
        if(command != null) {
            if(handlers.containsKey(command.getPath())) {
                if(handlers.get(command.getPath()).isDone()) {
                    startIndexer(command.getPath());
                }
            } else {
                startIndexer(command.getPath());
            }
        }*/
        if(!handlers.containsKey(IndexerConfigurationType.REVISION)) {
            startIndexer(IndexerConfigurationType.REVISION);
        } else {
            if(handlers.get(IndexerConfigurationType.REVISION).isDone()) {
                stopIndexer(IndexerConfigurationType.REVISION);
                startIndexer(IndexerConfigurationType.REVISION);
            }
        }
    }

    /**
     * Creates indexing commands for every revision in every language in database
     */
    public void indexEverything() {
        threadPool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*List<RevisionableEntity> entities = em.createQuery(
                        "SELECT e FROM RevisionableEntity e WHERE e.type " +
                                "IN ('"+ConfigurationType.PUBLICATION+"','"+ConfigurationType.SERIES+"','"+ConfigurationType.STUDY+"')", RevisionableEntity.class).getResultList();*/

                /*List<RevisionableEntity> entities = em.createQuery("SELECT e FROM RevisionableEntity e", RevisionableEntity.class).getResultList();*/

                // Stop the current indexer and clear index
                IndexerCommand command = RevisionIndexerCommand.stop();
                manager.getIndexDirectory(command.getPath(), true).clearIndex();
                commandRepository.addIndexerCommand(command);
                commandAdded(command);

                // Clear indexing info from all revisions
                revisions.clearAll();
                /*int current = 0;
                long timeSpent = 0L;
                for(RevisionableEntity entity : entities) {
                    long startTime = System.currentTimeMillis();
                    current++;
                    List<Integer> nos = revisions.getAllRevisionNumbers(entity.getId());
                    for(Integer no : nos) {
                        command = RevisionIndexerCommand.index(entity.getId(), no);
                        commandRepository.addIndexerCommand(command);
                        commandAdded(command);
                    }
                    long endTime = System.currentTimeMillis();
                    timeSpent += (endTime - startTime);
                    if(current % 1000 == 0) {
                        Logger.info(getClass(),"1000 revision index commands added to the queue in "+timeSpent+"ms. Still "+(entities.size()-current)+" commands to add.");
                        timeSpent = 0L;
                    }
                }*/
                return true;
            }
        });
    }

    /*public synchronized void addCommand(IndexerCommand command) {
        commandRepository.addIndexerCommand(command);
        if(handlers.containsKey(command.getPath())) {
            if(handlers.get(command.getPath()).isDone()) {
                startIndexer(command.getPath());
            }
        } else {
            startIndexer(command.getPath());
        }
    }*/

    public synchronized void commandAdded(IndexerCommand command) {
        if(handlers.containsKey(command.getPath().getType())) {
            if(handlers.get(command.getPath().getType()).isDone()) {
                startIndexer(command.getPath().getType());
            }
        } else {
            startIndexer(command.getPath().getType());
        }
    }

    public synchronized void startIndexer(IndexerConfigurationType type) {
        if(!isIndexerRunning(type)) {
            // Remove possible stopped handlers
            clearHandlers();

            revisions.clearPartlyIndexed();
            Indexer indexer = createIndexer(type);
            //indexers.put(path, indexer);
            handlers.put(type, threadPool.submit(indexer));
        }
    }

    public synchronized void clearHandlers() {
        for(Iterator<Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>>> i = handlers.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>> e = i.next();
            if(e.getValue().isDone()) {
                i.remove();
            }
        }
    }

    public synchronized void stopIndexers() {
        for(Iterator<Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>>> i = handlers.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>> e = i.next();
            if(e.getValue().cancel(true)) {
                i.remove();
            }
        }
    }

    public boolean hasRunningIndexers() {
        for(Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>> handler : handlers.entrySet()) {
            if(!handler.getValue().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean containsIndexer(IndexerConfigurationType type) {
        if(type != null) {
            return handlers.containsKey(type);
        }
        return false;
    }

    public boolean isIndexerRunning(IndexerConfigurationType type) {
        if(type != null && containsIndexer(type)) {
            return !handlers.get(type).isDone();
        }
        return false;
    }

    public void stopIndexer(IndexerConfigurationType type) {
        if(isIndexerRunning(type)) {
            handlers.get(type).cancel(true);
        }
    }

    public List<Pair<String, Boolean>> indexerStatusList() {
        List<Pair<String, Boolean>> list = new ArrayList<>();
        for(Map.Entry<IndexerConfigurationType, Future<IndexerStatusMessage>> handler : handlers.entrySet()) {
            list.add(new ImmutablePair<>(handler.getKey().toString(), !handler.getValue().isDone()));
        }
        return list;
    }

    /**
     * Returns an Indexer of the correct subclass based on the type
     * @param type
     * @return
     */
    private Indexer createIndexer(IndexerConfigurationType type) {
        Indexer indexer;
        switch(type)  {
            case REVISION:
                indexer = RevisionIndexer.build(manager, DirectoryManager.formPath(false, IndexerConfigurationType.REVISION), commandRepository, revisions, configurations, references);
                break;
            default:
                indexer = null;
                break;
        }
        return indexer;
    }
}
