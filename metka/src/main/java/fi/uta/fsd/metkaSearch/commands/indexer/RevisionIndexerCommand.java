package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.util.StringUtils;

public class RevisionIndexerCommand extends IndexerCommandBase {
    private static void checkAdditionalParams(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        if(path.getAdditionalParameters().length > 0) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too many additional parameters");
        }
    }

    // FACTORY METHODS
    /**
     * Constructs a RevisionIndexerCommand from database information.
     * @param path
     * @param action
     * @param parameters
     * @return
     * @throws UnsupportedOperationException
     */
    public static RevisionIndexerCommand fromParameterString(DirectoryManager.DirectoryPath path, Action action, String parameters) throws UnsupportedOperationException {
        switch(action) {
            case STOP:
                if(!StringUtils.hasText(parameters)) {
                    return stop(path);
                } else {
                    throw new UnsupportedOperationException("STOP action doesn't expect any parameters");
                }
            case REMOVE:
            case INDEX:
                if(!StringUtils.hasText(parameters)) {
                    throw new UnsupportedOperationException(action.name()+" expects more parameters");
                } else {
                    String[] params = parameters.split("/");
                    if(params == null || params.length != 2) {
                        throw new UnsupportedOperationException(action.name()+" expects exactly two parameters");
                    } else {
                        Long id = Long.parseLong(params[0]);
                        Integer no = Integer.parseInt(params[1]);
                        if(action == Action.REMOVE) {
                            return remove(path, id, no);
                        } else {
                            return index(path, id, no);
                        }
                    }
                }
            default:
                throw new UnsupportedOperationException("Action was not supported");
        }
    }

    // Stop commands
    public static RevisionIndexerCommand stop() {
        return stop(false);
    }

    /**
     * Forms a stop command for given configuration type and language
     * @param useRam
     * @return
     */
    public static RevisionIndexerCommand stop(boolean useRam) {
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(useRam, IndexerConfigurationType.REVISION);
        return stop(path);
    }

    /**
     * Factory method for stop command on revision indexer.
     *
     * @param path Indexer path for this command
     * @return RevisionIndexerCommand to stop wikipedia indexers
     */
    public static RevisionIndexerCommand stop(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        checkAdditionalParams(path);
        return new RevisionIndexerCommand(path, Action.STOP);
    }

    // Index commands

    public static RevisionIndexerCommand index(RevisionKey key) {
        return index(key.getId(), key.getNo());
    }
    public static RevisionIndexerCommand index(Long id, Integer no) {
        return index(id, no, false);
    }

    /**
     * Forms an index command for given configuration type and language
     * @param useRam
     * @return
     */
    public static RevisionIndexerCommand index(Long id, Integer no, boolean useRam) {
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(useRam, IndexerConfigurationType.REVISION);
        return index(path, id, no);
    }

    /**
     * Factory method for index command on wikipedia xml-dump file.
     *
     * @param path Indexer path for this command
     * @param id RevisionableId of the target revision
     * @param no Revision number of the target revision
     * @return WikipediaIndexerCommand to index a wikipedia xml-dump file
     */
    public static RevisionIndexerCommand index(DirectoryManager.DirectoryPath path, Long id, Integer no) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        checkAdditionalParams(path);
        return new RevisionIndexerCommand(path, Action.INDEX, id, no);
    }


    // Remove commands

    public static RevisionIndexerCommand remove(RevisionKey key) {
        return remove(key.getId(), key.getNo());
    }
    public static RevisionIndexerCommand remove(Long id, Integer no) {
        return remove(id, no, false);
    }

    /**
     * Forms a remove command for given configuration type and language
     * @param useRam
     * @return
     */
    public static RevisionIndexerCommand remove(Long id, Integer no, boolean useRam) {
        DirectoryManager.DirectoryPath path = DirectoryManager.formPath(useRam, IndexerConfigurationType.REVISION);
        return remove(path, id, no);
    }

    /**
     * Factory method for remove command on wikipedia page.
     *
     * @param path Indexer path for this command
     * @param revisionable RevisionableId of the target revision
     * @param revision Revision number of the target revision
     * @return WikipediaIndexerCommand to remove a wikipedia page from index
     */
    public static RevisionIndexerCommand remove(DirectoryManager.DirectoryPath path, Long revisionable, Integer revision) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        checkAdditionalParams(path);
        return new RevisionIndexerCommand(path, Action.REMOVE, revisionable, revision);
    }

    // COMMAND IMPLEMENTATION
    private final Long id;
    private final Integer no;

    private RevisionIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        this(path, action, null, null);
    }

    // Command should only be formed through factory methods
    private RevisionIndexerCommand(DirectoryManager.DirectoryPath path, Action action, Long id, Integer no) {
        super(path, action);
        this.id = id;
        this.no = no;
    }

    public Long getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    @Override
    public String toParameterString() {
        switch(getAction()) {
            case STOP:
                return "";
            case REMOVE:
            case INDEX:
                return id +"/"+ no;
            default:
                return "";
        }
    }
}
