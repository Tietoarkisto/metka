package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metkaSearch.commands.searcher.SearcherCommand;

public interface SearcherCommandHandler {
    /**
     * Adds searcher command to some sort of work queue.
     * Should check that it can be handled in the context where it's called.
     * TODO: There should be some sort of result return mechanism
     * @param command Command to be added
     * @return false if adding the command failed for some reason (queue full or wrong type for example) true otherwise
     */
    boolean addCommand(SearcherCommand command);
}
