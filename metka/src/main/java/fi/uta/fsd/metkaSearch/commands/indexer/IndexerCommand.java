package fi.uta.fsd.metkaSearch.commands.indexer;

import fi.uta.fsd.metkaSearch.directory.DirectoryManager;

/**
 * Base interface for all Indexer commands.
 * Defines getters for two attributes required of all Indexer Commands:
 *     path - DirectoryInformation path to which this command is linked. This informs the system about to which indexer
 *            the command should go to.
 *     action - Action enum value that lets the indexer know what it should do.
 *
 * It is not possible to form any documents based on these parameters alone so all implementing classes should contain
 * type specific additional information that allows for actual document creation and indexing or document identification
 * from index in case of remove actions.
 */
public interface IndexerCommand {
    public DirectoryManager.DirectoryPath getPath();

    public Action getAction();

    /**
     * Defines what indexer should do with given command.
     * Current actions are index, remove and stop. Remove clears the document from index, stop requests the indexer to stop
     * and index adds the document to index if it's not present or creates the document, then removes the old one from index
     * and adds the new one in its stead if already present.
     * If more actions are needed they are added here.
     * Each action should correspond to a single set of instructions followed by the indexer. In this light it might make
     * sense to have INDEX only produce a document and then add one or two new commands to the queue in the form of REMOVE
     * if there was a previous version of the document in the index (basically if we are reindexing a document) and ADD
     * which contains the indexed document. This might be overkill however and will not be implemented until the other
     * parts of the program are more clear.
     */
    public static enum Action {
        INDEX,
        REMOVE,
        STOP
        // ...
    }
}
