package fi.uta.fsd.metkaSearch;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;
import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandEntity;
import fi.uta.fsd.metkaSearch.entity.IndexerCommandRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.enums.IndexerStatusMessage;
import fi.uta.fsd.metkaSearch.indexers.DummyIndexer;
import fi.uta.fsd.metkaSearch.indexers.Indexer;
import fi.uta.fsd.metkaSearch.indexers.RevisionIndexer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class IndexerComponent {


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

    @Autowired
    private DirectoryManager manager;

    @Autowired
    private StudyErrorsRepository studyErrors;

    @Autowired
    private BinderRepository binders;

    // Pool for indexer threads.
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    /*private final Map<RevisionKey, Boolean> studyCommandBatch = new ConcurrentHashMap<>();*/

    /*private volatile boolean runningBatch = false;*/

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
            } else {
                startIndexer(command.getPath());
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
                List<RevisionableEntity> entities = em.createQuery(
                        "SELECT e FROM RevisionableEntity e WHERE e.type " +
                                "IN ('"+ConfigurationType.PUBLICATION+"','"+ConfigurationType.SERIES+"','"+ConfigurationType.STUDY+"')", RevisionableEntity.class).getResultList();
                for(ConfigurationType type : ConfigurationType.values()) {
                    for(Language language : Language.values()) {
                        IndexerCommand command = RevisionIndexerCommand.stop(type, language);
                        manager.getIndexDirectory(command.getPath(), true).clearIndex();
                        addCommand(command);
                    }
                }
                int current = 0;
                long timeSpent = 0L;
                for(RevisionableEntity entity : entities) {
                    long startTime = System.currentTimeMillis();
                    current++;
                    List<Integer> nos = revisions.getAllRevisionNumbers(entity.getId());
                    for(Integer no : nos) {
                        for(Language language : Language.values()) {
                            addCommand(RevisionIndexerCommand.index(ConfigurationType.fromValue(entity.getType()), language, entity.getId(), no));
                        }
                    }
                    long endTime = System.currentTimeMillis();
                    timeSpent += (endTime - startTime);
                    if(current % 1000 == 0) {
                        Logger.info(getClass(),"1000 revision index commands added to the queue in "+timeSpent+"ms. Still "+(entities.size()-current)+" commands to add.");
                        timeSpent = 0L;
                    }
                }
                return true;
            }
        });
    }

    public synchronized void addCommand(IndexerCommand command) {
        // TODO: make a better rule
        if(command.getPath().getAdditionalParameters().length > 0) {
            switch(command.getPath().getAdditionalParameters()[0]) {
                case "STUDY":
                case "SERIES":
                case "PUBLICATION":
                    // We allow these types
                    break;
                default:
                    return;
            }
        }
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
            handlers.put(path, threadPool.submit(indexer));
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
            case DUMMY:
                indexer = DummyIndexer.build(manager, path, commandRepository);
                break;
            case REVISION:
                indexer = RevisionIndexer.build(manager, path, commandRepository, revisions, configurations, references, studyErrors, binders);
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
            Logger.error(getClass(), "Tried to add index command for study with id " + id + " but didn't find any revisions with result " + pair.getLeft());
            return;
        }
        for(Language lang : Language.values()) {
            if (index) {
                addCommand(RevisionIndexerCommand.index(ConfigurationType.STUDY, lang, pair.getRight().getKey()));
            } else {
                addCommand(RevisionIndexerCommand.remove(ConfigurationType.STUDY, lang, pair.getRight().getKey()));
            }
        }
    }

    /*@Scheduled(fixedDelay = 1 * 60 *1000)
    private void executeStudyBatch() {
        runningBatch = true;
        for(RevisionKey key : studyCommandBatch.keySet()) {
            for(Language lang : Language.values()) {
                if (studyCommandBatch.get(key)) {
                    addCommand(RevisionIndexerCommand.index(ConfigurationType.STUDY, lang, key));
                } else {
                    addCommand(RevisionIndexerCommand.remove(ConfigurationType.STUDY, lang, key));
                }
            }
        }
        studyCommandBatch.clear();
        runningBatch = false;
    }*/

    public Pair<ReturnResult, Integer> getOpenIndexCommands() {
        List<IndexerCommandEntity> entities = em
                .createQuery("SELECT e FROM IndexerCommandEntity e WHERE e.handled IS NULL AND e.requested IS NULL", IndexerCommandEntity.class)
                .getResultList();

        return new ImmutablePair<>(ReturnResult.OPERATION_SUCCESSFUL, entities.size());
    }
}
