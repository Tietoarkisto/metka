package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.RevisionRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO: Add language support

@Component
public class RestrictionValidator {
    private static final Logger logger = LoggerFactory.getLogger(RestrictionValidator.class);
    @Autowired
    private SearcherComponent searcher;

    @Autowired
    private ConfigurationRepository configurations;

    @Autowired
    private RevisionRepository revisions;

    public boolean validate(RevisionData revision, List<Target> targets, Configuration configuration) {
        initParents(revision, targets);
        /*Pair<ReturnResult, Configuration> pair = configurations.findConfiguration(revision.getConfiguration());
        if(pair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            throw new UnsupportedOperationException("Could not find configuration for "+revision.toString());
        }*/
        //DataFieldValidator validator = new DataFieldValidator(revision, pair.getRight(), this, searcher, revisions);
        DataFieldValidator validator = new DataFieldValidator(searcher, revisions, configurations);
        return validator.validate(targets, revision, configuration);
    }

    /**
     * Calls init parent on all targets in list. Can be redundant but shouldn't take that much time.
     * This is required since we need to be able to navigate upwards in target tree to check the context of conditions.
     * @param targets   List of Targets
     */
    private void initParents(RevisionData revision, List<Target> targets) {
        revision.initParents();
        for(Target t : targets) {
            t.initParents();
        }
    }


}
