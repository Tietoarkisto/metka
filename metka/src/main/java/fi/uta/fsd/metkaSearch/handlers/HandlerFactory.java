package fi.uta.fsd.metkaSearch.handlers;

import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.BinderRepository;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.StudyErrorsRepository;
import fi.uta.fsd.metkaSearch.enums.IndexerConfigurationType;
import fi.uta.fsd.metkaSearch.indexers.Indexer;

public final class HandlerFactory {
    public static RevisionHandler buildRevisionHandler(Indexer indexer, RevisionRepository revisions,
                                                       ConfigurationRepository configurations, ReferenceService references,
                                                       StudyErrorsRepository studyErrors, BinderRepository binders) throws UnsupportedOperationException {
        // Make some sanity checks
        if(indexer == null || indexer.getPath().getType() != IndexerConfigurationType.REVISION) {
            throw new UnsupportedOperationException("Needs an indexer with path type REVISION");
        }
        if(revisions == null || configurations == null || references == null) {
            throw new UnsupportedOperationException("Needs all three: general repository, configuration repository and reference service");
        }

        return new GeneralRevisionHandler(indexer, revisions, configurations, references, studyErrors, binders);
    }

    // Private constructor to stop instantiation
    private HandlerFactory() {}
}
