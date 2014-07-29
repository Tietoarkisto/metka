package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;

public interface RevisionHandler {

    /**
     * Handles the given data in the way specified in the implementing handler
     * @return Was data added to the index
     */
    public boolean handle(RevisionIndexerCommand command);
}
