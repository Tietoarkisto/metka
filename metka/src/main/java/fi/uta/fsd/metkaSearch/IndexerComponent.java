package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.indexers.DummyIndexer;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.RevisionIndexer;
import fi.uta.fsd.metkaSearch.indexers.WikipediaIndexer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class IndexerComponent {
    private static final Logger logger = LoggerFactory.getLogger(IndexerComponent.class);

    @PersistenceContext(name = "entityManager")
    private EntityManager em;

    @Autowired
    private RevisionRepository revisions;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private IndexerCommandRepository commandRepository;

    @Autowired
    private ReferenceService references;

    // Pool for indexer threads.
    private ExecutorService indexerPool = Executors.newCachedThreadPool();

    private final Map<RevisionKey, IndexerCommand> studyCommandBatch = new ConcurrentHashMap<>();

    private volatile boolean runningBatch = false;

    /**
     * Map of indexers.
     * Each indexer handles a single index based on its IndexerConfigurationType
     * and takes care of commands linked to that index.
     * All objects within a single index should be parsed for indexing using similar procedures.
     * All objects within a single index should also be searchable using similar procedures.
     */
    private final Map<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> handlers = new ConcurrentHashMap<>();

    /**
     * Executed at program startup. Clears requested status from all non handled methods and removes all handled methods from database.
     */
    @PostConstruct
    public void clearCommands() {
        commandRepository.clearAllRequests();
        commandRepository.removeAllHandled();

        // Clear locks
        for(Language lang : Language.values()) {
            for(ConfigurationType type : ConfigurationType.values()) {
                DirectoryManager.DirectoryPath path = DirectoryManager
                        .formPath(false, IndexerConfigurationType.REVISION, lang, type.toValue());
                try {
                    DirectoryInformation info = new DirectoryInformation(path, false);
                    logger.info("Checking directory "+path+" for write lock.");
                    if(IndexWriter.isLocked(info.getDirectory())) {
                        logger.info("Directory "+path+" contained lock. Attempting to clear lock with name "+IndexWriter.WRITE_LOCK_NAME+" from directory.");
                        IndexWriter.unlock(info.getDirectory());
                        info.getDirectory().clearLock(IndexWriter.WRITE_LOCK_NAME);
                        if(IndexWriter.isLocked(info.getDirectory())) {
                            logger.error("FAIL during lock clearing for path "+path+" attempting forced delete since we know that the lock should not be in use");
                            info.getDirectory().deleteFile(IndexWriter.WRITE_LOCK_NAME);
                        } else {
                            logger.error("SUCCESS during lock clearing for path "+path);
                        }
                    }
                } catch(Exception e) {
                    logger.error("Exception while clearing path "+path+" from write lock:", e);
                }
            }
        }
    }

    /**
     * Checks for commands that have handlers that have stopped for one reason or another.
     * Also checks requested commands that have not been repeated for stopped handles and
     * if stopped handler is found then marks the command as repeated, removes the requested
     * timestamp and refires the indexer for that command.
     */
    @Scheduled(fixedDelay = 5000)
    public void checkIndexers() {
        //System.err.println("Checking for commands on stopped indexers");
        // TODO: repeat try
        IndexerCommand command = commandRepository.getNextCommandWithoutChange();
        if(command != null) {
            if(handlers.containsKey(command.getPath())) {
                if(handlers.get(command.getPath()).isDone()) {
                    startIndexer(command.getPath());
                }
            }
        }
    }

    /**
     * Creates indexing commands for every revision in every language in database
     */
    public void indexEverything() {
        List<RevisionableEntity> entities = em.createQuery("SELECT e FROM RevisionableEntity e", RevisionableEntity.class).getResultList();
        List<IndexerCommand> commands = new ArrayList<>();
        for(RevisionableEntity entity : entities) {
            List<Integer> nos = revisions.getAllRevisionNumbers(entity.getId());
            for(Integer no : nos) {
                for(Language language : Language.values()) {
                    commands.add(RevisionIndexerCommand.index(ConfigurationType.fromValue(entity.getType()), language, entity.getId(), no));
                }
            }
        }
        for(ConfigurationType type : ConfigurationType.values()) {
            for(Language language : Language.values()) {
                IndexerCommand command = RevisionIndexerCommand.stop(type, language);
                DirectoryManager.getIndexDirectory(command.getPath(), true).clearIndex();
                addCommand(command);
            }
        }
        for(IndexerCommand command : commands) {
            addCommand(command);
        }
    }

    public void addCommand(IndexerCommand command) {
        commandRepository.addIndexerCommand(command);
        if(handlers.containsKey(command.getPath())) {
            if(handlers.get(command.getPath()).isDone()) {
                startIndexer(command.getPath());
            }
        } else {
            startIndexer(command.getPath());
        }
    }

    public void startIndexer(DirectoryManager.DirectoryPath path) {
        if(!isIndexerRunning(path)) {
            // Remove possible stopped handlers
            clearHandlers();

            Indexer indexer = createIndexer(path);
            //indexers.put(path, indexer);
            handlers.put(path, indexerPool.submit(indexer));
        }
    }

    public void clearHandlers() {
        for(Iterator<Map.Entry<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>>> i = handlers.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> e = i.next();
            if(e.getValue().isDone()) {
                i.remove();
            }
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
        if(path != null && StringUtils.hasText(path.toString())) {
            return handlers.containsKey(path);
        }
        return false;
    }

    public boolean isIndexerRunning(DirectoryManager.DirectoryPath path) {
        if(path != null && StringUtils.hasText(path.toString()) && containsIndexer(path)) {
            return !handlers.get(path).isDone();
        }
        return false;
    }

    public void stopIndexer(DirectoryManager.DirectoryPath path) {
        if(isIndexerRunning(path)) {
            handlers.get(path).cancel(true);
        }
    }

    public List<Pair<String, Boolean>> indexerStatusList() {
        List<Pair<String, Boolean>> list = new ArrayList<>();
        for(Map.Entry<DirectoryManager.DirectoryPath, Future<IndexerStatusMessage>> handler : handlers.entrySet()) {
            list.add(new ImmutablePair<>(handler.getKey().toString(), !handler.getValue().isDone()));
        }
        return list;
    }

    /**
     * Returns an Indexer of the correct subclass based on the type
     * @param path
     * @return
     */
    private Indexer createIndexer(DirectoryManager.DirectoryPath path) {
        Indexer indexer = null;
        switch(path.getType())  {
            case WIKIPEDIA:
                indexer = WikipediaIndexer.build(path, commandRepository);
                break;
            case DUMMY:
                indexer = DummyIndexer.build(path, commandRepository);
                break;
            case REVISION:
                indexer = RevisionIndexer.build(path, commandRepository, revisions, configurations, references);
                break;
            default:
                indexer = null;
                break;
        }
        return indexer;
    }

    public void addStudyIndexerCommand(Long id, boolean index) {
        Pair<ReturnResult, RevisionData> pair = revisions.getLatestRevisionForIdAndType(id, false, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            logger.error("Tried to add index command for study with id "+id+" but didn't find any revisions with result "+pair.getLeft());
            return;
        }
        try {
            // Wait while the current batch is being handled
            while(runningBatch = true) {
                Thread.sleep(500);
            }
            if(studyCommandBatch.containsKey(pair.getRight().getKey())) {
                return;
            }
            for(Language lang : Language.values()) {
                if (index) {
                    studyCommandBatch.put(pair.getRight().getKey(), RevisionIndexerCommand.index(ConfigurationType.STUDY, lang, pair.getRight().getKey()));
                } else {
                    studyCommandBatch.put(pair.getRight().getKey(), RevisionIndexerCommand.remove(ConfigurationType.STUDY, lang, pair.getRight().getKey()));
                }
            }
        } catch(InterruptedException ie) {
            // Well damn, let's not add the index command then
        }
    }

    @Scheduled(fixedDelay = 1 * 60 *1000)
    private void executeStudyBatch() {
        runningBatch = true;
        for(IndexerCommand command : studyCommandBatch.values()) {
            addCommand(command);
        }
        studyCommandBatch.clear();
        runningBatch = false;
    }
}
