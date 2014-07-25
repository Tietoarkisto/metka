package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metkaSearch.commands.indexer.RevisionIndexerCommand;

import java.io.IOException;

public interface RevisionHandler {

    /**
     * Handles the given data in the way specified in the implementing handler
     * @return Was data added to the index
     * @throws IOException This usually happens when there's a problem with getting the indexWriter
     */
    public boolean handle(RevisionIndexerCommand command) throws IOException;
}
