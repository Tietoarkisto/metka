package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.domain.ReferenceService;
import fi.uta.fsd.metkaSearch.directory.DirectoryInformation;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;

public final class HandlerFactory {
    public static RevisionHandler buildRevisionHandler(DirectoryInformation indexer, RevisionData data, Configuration config, ReferenceService references, Pair<Boolean, LocalDateTime> removalInfo) throws UnsupportedOperationException {
        // Make some sanity checks
        if(indexer == null || indexer.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Needs an indexer with path type REVISION");
        }
        if(data == null || config == null) {
            throw new UnsupportedOperationException("Need both data and configuration");
        }
        if(!data.getConfiguration().equals(config.getKey())) {
            throw new UnsupportedOperationException("Given configuration is not for given data");
        }

        return new GeneralRevisionHandler(indexer, data, config, references, removalInfo);
    }

    // Private constructor to stop instantiation
    private HandlerFactory() {}
}
