package fi.uta.fsd.metkaSearch.enums;

/**
 * Type separates different indexers and their commands from one another.
 * Indexing can be targeted straight to revisions, wikipedia-files (for testing purposes), json-files and possibly other types.
 * Each command subclass knows information that needs to be provided to the indexer for successful indexing.
 * This information can contain things like object keys, indexing configurations, content for indexing etc.
 */
public enum IndexerConfigurationType {
    DUMMY, // Used only for simple testing, doesn't do anything
    REVISION, // Used to index top level information about a revision
    CONTAINER, // Used to index container information from revision
    REFERENCECONTAINER, // Used to index reference container information from revision
    WIKIPEDIA, // Should be removed from production
    JSON
    // ...
}