package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;

import java.io.IOException;

public interface RevisionHandler {
    public DirectoryInformation getIndexer();

    public RevisionData getData();

    public Configuration getConfig();

    /**
     * Handles the given data in the way specified in the implementing handler
     * @return Was data added to the index
     * @throws IOException This usually happens when there's a problem with getting the indexWriter
     */
    public boolean handle() throws IOException;
}
