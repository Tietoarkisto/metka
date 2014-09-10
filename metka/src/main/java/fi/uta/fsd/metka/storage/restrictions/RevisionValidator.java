package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class RevisionValidator {
    private static final Logger logger = LoggerFactory.getLogger(RevisionValidator.class);
    private final RevisionData revision;
    private final RestrictionValidator validator;
    private final SearcherComponent searcher;
    private final Configuration configuration;
    private final RevisionRepository revisions;

    RevisionValidator(RevisionData revision, Configuration configuration, RestrictionValidator validator, SearcherComponent searcher, RevisionRepository revisions) {
        this.revision = revision;
        this.configuration = configuration;
        this.validator = validator;
        this.searcher = searcher;
        this.revisions = revisions;
    }

    boolean validate(List<Target> targets, DataFieldContainer context) {
        for(Target target : targets) {
            if(!validateTarget(target, context)) {
                return false;
            }
        }
        return true;
    }

    boolean validateTarget(Target target, DataFieldContainer context) {
        switch(target.getType()) {
            case QUERY:
                return QueryTargetHandler.handle(target, revision, validator, searcher, configuration);
            case VALUE:
                // VALUE type target at this point will return true straight away since it doesn't restrict the operation in any way.
                return ValueTargetHandler.handle(target);
            case NAMED:
                return NamedTargetHandler.handle(target, configuration, context, this);
            case FIELD:
                return FieldTargetHandler.handle(target, context, this, validator, revision, configuration, revisions, searcher);
            default:
                // Should not be reached since all possibilities are accounted for but causes a failure
                logger.error("Reached TargetType "+target.getType());
                return false;
        }
    }
}
