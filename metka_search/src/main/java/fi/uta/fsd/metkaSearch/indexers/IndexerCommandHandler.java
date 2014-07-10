package fi.uta.fsd.metkaSearch.indexers;

import fi.uta.fsd.metkaSearch.commands.indexer.IndexerCommand;

public interface IndexerCommandHandler {
    /**
     * Adds indexer command to some sort of work queue.
     * Should check that it can be handled in the context where it's called.
     * @param command Command to be added
     * @return false if adding the command failed for some reason (queue full or wrong type for example) true otherwise
     */
    boolean addCommand(IndexerCommand command);
}
