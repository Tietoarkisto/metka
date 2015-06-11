package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metkaSearch.SearcherComponent;

import java.util.List;

class DataFieldValidator {
    private final SearcherComponent searcher;
    private final RevisionRepository revisions;
    private final ConfigurationRepository configurations;

    DataFieldValidator(SearcherComponent searcher, RevisionRepository revisions, ConfigurationRepository configurations) {
        this.searcher = searcher;
        this.revisions = revisions;
        this.configurations = configurations;
    }

    boolean validate(List<Target> targets, DataFieldContainer context, Configuration configuration) {
        for(Target target : targets) {
            if(!validate(target, context, configuration)) {
                return false;
            }
            /*if(!validate(target.getTargets(), context, configuration)) {
                return false;
            }*/
        }
        return true;
    }

    boolean validate(Target target, DataFieldContainer context, Configuration configuration) {
        switch(target.getType()) {
            case NAMED:
                return NamedTargetHandler.handle(target, configuration, context, this);
            case PARENT:
                return ParentTargetHandler.handle(this, target, context, configuration, configurations);
            case QUERY:
                return QueryTargetHandler.handle(target, context, this, configuration, searcher);
            case FIELD:
                return FieldTargetHandler.handle(target, context, this, configuration, searcher, revisions, configurations);
            case CHILDREN:
                return ChildrenTargetHandler.handle(this, target, context, configuration, revisions, configurations);
            default:
                // This catches types that are invalid for this point of validation operation such as VALUE
                Logger.error(getClass(), "Reached TargetType " + target.getType());
                return false;

        }
    }
}
