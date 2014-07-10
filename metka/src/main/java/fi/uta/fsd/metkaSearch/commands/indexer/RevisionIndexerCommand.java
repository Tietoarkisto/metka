package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;

import java.io.IOException;

public class RevisionIndexerCommand extends IndexerCommandBase {
    // FACTORY METHODS
    /**
     * Factory method for stop command on revision indexer.
     *
     * @param path Indexer path for this command
     * @return RevisionIndexerCommand to stop wikipedia indexers
     */
    public static RevisionIndexerCommand stop(DirectoryManager.DirectoryPath path) throws UnsupportedOperationException {
        checkPathType(path, IndexerConfigurationType.REVISION);
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
}
