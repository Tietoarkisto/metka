package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.springframework.util.StringUtils;

public class RevisionIndexerCommand extends IndexerCommandBase {
    private static void checkAdditionalParams(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        if(path.getAdditionalParameters() == null || path.getAdditionalParameters().length == 0) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too few additional parameters");
        }
        if(path.getAdditionalParameters().length > 1) {
            // There has to be one and only one additional parameter
            throw new UnsupportedOperationException("Too many additional parameters");
        }
        if(!ConfigurationType.isValue(path.getAdditionalParameters()[0])) {
            // Additional parameter must be a String representation of one of the ConfigurationTypes
            throw new UnsupportedOperationException("Additional parameter doesn't match any configuration type");
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
                if(StringUtils.isEmpty(parameters)) {
                    return stop(path);
                } else {
                    throw new UnsupportedOperationException("STOP action doesn't expect any parameters");
                }
            case REMOVE:
            case INDEX:
                if(StringUtils.isEmpty(parameters)) {
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

    /**
     * Factory method for index command on wikipedia xml-dump file.
     *
     * @param path Indexer path for this command
     * @param revisionable RevisionableId of the target revision
     * @param revision Revision number of the target revision
     * @return WikipediaIndexerCommand to index a wikipedia xml-dump file
     */
    public static RevisionIndexerCommand index(DirectoryManager.DirectoryPath path, Long revisionable, Integer revision) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
        checkAdditionalParams(path);
        return new RevisionIndexerCommand(path, Action.INDEX, revisionable, revision);
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
    private final Long  revisionable;
    private final Integer revision;

    private RevisionIndexerCommand(DirectoryManager.DirectoryPath path, Action action) {
        this(path, action, null, null);
    }

    // Command should only be formed through factory methods
    private RevisionIndexerCommand(DirectoryManager.DirectoryPath path, Action action, Long revisionable, Integer revision) {
        super(path, action);
        this.revisionable = revisionable;
        this.revision = revision;
    }

    public Long getRevisionable() {
        return revisionable;
    }

    public Integer getRevision() {
        return revision;
    }

    @Override
    public String toParameterString() {
        switch(getAction()) {
            case STOP:
                return "";
            case REMOVE:
            case INDEX:
                return revisionable+"/"+revision;
            default:
                return "";
        }
    }
}
