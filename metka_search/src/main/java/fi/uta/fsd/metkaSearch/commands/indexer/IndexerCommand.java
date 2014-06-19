package fi.uta.fsd.metkaSearch.commands.indexer;

/**
 * Abstract base class for all Indexer commands.
 * Contains final parameters:
 *     type - Type enum value that should be provided by the constructor in implementing class
 *     action - Action enum value that should be provided to the constructor from outside of the implementing class
 *              and let's the indexer know what it should do.
 *
 * It is not possible to form any documents based on these parameters alone so all implementing classes should contain
 * type specific additional information that allows for actual document creation and indexing or document identification
 * from index in case of remove actions.
 */
public interface IndexerCommand {
    public Type getType();

    public Action getAction();

    /**
     * Type separates different Commands from one another.
     * Indexing can be targeted straight to revisions, xml-files, json-files and possibly other types.
     * Each command subclass knows information that needs to be provided to the indexer for successful indexing.
     * This information can contain things like object keys, indexing configurations, content for indexing etc.
     */
    public static enum Type {
        REVISION,
        XML,
        JSON
        // ...
    }

    /**
     * Defines what indexer should do with given command.
     * For now actions are only index and remove. Remove clears the document from index, Index adds the document to index
     * if it's not present or creates the document, then removes the old one from index and adds the new one in its stead
     * if already present.
     * If more actions are needed they are added here.
     * Each action should correspond to a single set of instructions followed by the indexer. In this light it might make
     * sense to have INDEX only produce a document and then add one or two new commands to the queue in the form of REMOVE
     * if there was a previous version of the document in the index (basically if we are reindexing a document) and ADD
     * which contains the indexed document. This might be overkill however and will not be implemented until the other
     * parts of the program are more clear.
     */
    public static enum Action {
        INDEX,
        REMOVE
        // ...
    }
}
