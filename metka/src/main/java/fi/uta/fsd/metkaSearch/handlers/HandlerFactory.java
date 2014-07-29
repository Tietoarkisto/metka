package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.GeneralRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.indexers.Indexer;

public final class HandlerFactory {
    public static RevisionHandler buildRevisionHandler(Indexer indexer, GeneralRepository generalRepo, ConfigurationRepository configurations, ReferenceService references) throws UnsupportedOperationException {
        // Make some sanity checks
        if(indexer == null || indexer.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Needs an indexer with path type REVISION");
        }
        if(generalRepo == null || configurations == null || references == null) {
            throw new UnsupportedOperationException("Needs all three: general repository, configuration repository and reference service");
        }

        return new GeneralRevisionHandler(indexer, generalRepo, configurations, references);
    }

    // Private constructor to stop instantiation
    private HandlerFactory() {}
}
