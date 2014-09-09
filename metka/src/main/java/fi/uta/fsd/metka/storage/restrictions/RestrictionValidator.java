package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.access.calls.ContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ReferenceContainerDataFieldCall;
import fi.uta.fsd.metka.model.access.calls.ValueDataFieldCall;
import fi.uta.fsd.metka.model.configuration.*;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ContainerDataField;
import fi.uta.fsd.metka.model.data.container.DataField;
import fi.uta.fsd.metka.model.data.container.ReferenceContainerDataField;
import fi.uta.fsd.metka.model.data.container.ValueDataField;
import fi.uta.fsd.metka.storage.repository.ConfigurationRepository;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
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

    public boolean validate(RevisionData revision, List<Target> targets) {
        initParents(targets);
        RevisionValidator validator = new RevisionValidator(revision, configurations, this, searcher);
        return validator.validate(targets);
    }

    /**
     * Calls init parent on all targets in list. Can be redundant but shouldn't take that much time.
     * This is required since we need to be able to navigate upwards in target tree to check the context of conditions.
     * @param targets   List of Targets
     */
    private void initParents(List<Target> targets) {
        for(Target t : targets) {
            t.initParents();
        }
    }

    private static class RevisionValidator {
        private final RevisionData revision;
        private final ConfigurationRepository configurations;
        private final RestrictionValidator validator;
        private final SearcherComponent searcher;

        private final Configuration configuration;

        private RevisionValidator(RevisionData revision, ConfigurationRepository configurations, RestrictionValidator validator, SearcherComponent searcher) {
            this.revision = revision;
            this.configurations = configurations;
            this.validator = validator;
            this.searcher = searcher;

            Pair<ReturnResult, Configuration> configurationPair = configurations.findConfiguration(revision.getConfiguration());
            if(configurationPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
                throw new UnsupportedOperationException("Could not find configuration "+revision.getConfiguration().toString()+" for revision "+revision.toString());
            }
            configuration = configurationPair.getRight();
        }

        private boolean validate(List<Target> targets) {
            for(Target target : targets) {
                if(!validateTarget(target)) {
                    return false;
                }
            }
            return true;
        }

        private boolean validateTarget(Target target) {
            switch(target.getType()) {
                case QUERY:
                    return handleQueryTarget(target);
                case VALUE:
                    // VALUE type target at this point will return true straight away since it doesn't restrict the operation in any way.
                    return true;
                case NAMED:
                    return handleNamedTarget(target);
                case FIELD:
                    boolean result = handleFieldTarget(target);
                    if(result) {
                        // TODO: If field points to a reference to another Revisionable then fetch a revision and perform the validation on that
                        result = validate(target.getTargets());
                    }
                    return result;
                default:
                    // Should not be reached since all possibilities are accounted for but causes a failure
                    return false;
            }
        }

        // TARGE HANDLERS
        //******************************

        /**
         * Fetches the named Target from configuration. If it is found copies it, sets the copy's parent to target's parent
         * and calls validateTarget with the new target. So simply replaces the NAMED target with a 'template' and handles it like it was always present.
         * @param target    NAMED type Target
         * @return Boolean if the Target validates to true
         */
        private boolean handleNamedTarget(Target target) {
            Target named = configuration.getNamedTargets().get(target.getContent());
            if(named == null) {
                return false;
            }
            named = named.copy();
            named.setParent(target.getParent());
            named.initParents();
            return validateTarget(named);
        }

        private boolean handleFieldTarget(Target target) {
            Field field = configuration.getField(target.getContent());
            if(field == null) {
                return false;
            }
            if(field.getSubfield() && target.getParent() == null) {
                // Something is wrong. Field is marked as a subfield but target doesn't have a parent
                return false;
            }
            if(!field.getSubfield()) {
                return handleTopLevelField(field, target);
            } else {
                return true;
                //return handleRowField(field, target);
            }
        }

        private boolean handleTopLevelField(Field field, Target target) {
            DataField d = null;
            switch (field.getType()) {
                case CONTAINER:
                    d = revision.dataField(ContainerDataFieldCall.get(target.getContent())).getRight();
                    break;
                case REFERENCECONTAINER:
                    d = revision.dataField(ReferenceContainerDataFieldCall.get(target.getContent())).getRight();
                    break;
                default:
                    d = revision.dataField(ValueDataFieldCall.get(target.getContent())).getRight();
                    break;
            }
            for(Check check : target.getChecks()) {
                // Check is enabled
                if(validator.validate(revision, check.getRestrictors())) {
                    if(!checkConditionForField(field, d, check.getCondition())) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean handleQueryTarget(Target target) {
            ResultList<? extends SearchResult> result = performQuery(target.getContent());
            for(Check check : target.getChecks()) {
                // Check is enabled
                if(validator.validate(revision, check.getRestrictors())) {
                    if(!checkConditionForQuery(result, check.getCondition())) {
                        return false;
                    }
                }
            }
            return true;
        }

        private ResultList<? extends SearchResult> performQuery(String query) {
            query = query.replace("{id}", revision.getKey().getId().toString());
            try {
                return searcher.executeSearch(ExpertRevisionSearchCommand.build(query, configurations));
            } catch (QueryNodeException qne) {
                logger.error("Exception while performing query: "+query);
                return new ListBasedResultList<>(ResultList.ResultType.REVISION);
            }
        }

        // CONDITION HANDLERS
        // *************************
        private boolean checkConditionForField(Field field, DataField d, Condition condition) {
            switch(field.getType()) {
                case CONTAINER:
                    return checkConditionForContainerField((ContainerDataField)d, condition);
                case REFERENCECONTAINER:
                    return checkConditionForReferenceContainerField((ReferenceContainerDataField)d, condition);
                default:
                    return checkConditionForValueField((ValueDataField)d, condition);
            }
        }

        private boolean checkConditionForContainerField(ContainerDataField d, Condition condition) {
            switch(condition.getType()) {
                case IS_EMPTY:
                    return handleIsEmpty(d);
                case NOT_EMPTY:
                    return handleNotEmpty(d);
                default:
                    return true;
            }
        }

        private boolean checkConditionForReferenceContainerField(ReferenceContainerDataField d, Condition condition) {
            switch(condition.getType()) {
                case IS_EMPTY:
                    return handleIsEmpty(d);
                case NOT_EMPTY:
                    return handleNotEmpty(d);
                default:
                    return true;
            }
        }

        private boolean checkConditionForValueField(ValueDataField d, Condition condition) {
            switch(condition.getType()) {
                case IS_EMPTY:
                    return handleIsEmpty(d);
                case NOT_EMPTY:
                    return handleNotEmpty(d);
                case UNIQUE:
                    // TODO: Check if field is a subfield or top level field.

                    // TODO: If field is top level field then perform lucene search to see if value is unique between revisionables

                    // TODO: If field is a subfield field then fetch the immediate parent (if it exists) and go through all the rows making sure the value in the
                    // TODO: field is unique amongst rows
                    return true;
                case INCREASING:
                    // TODO: Check if field is a subfield or top level field.

                    // TODO: If field is top level field then fetch previous revision (save or approve operations can only modify the latest revision so if we check
                    // TODO: against the previous revision we should always be valid) then check if the current value is higher than (or equal to if we support value
                    // TODO: staying the same for multiple revisions) previous value.

                    // TODO: If field is a subfield field then fetch the immediate parent (if it exists) and go through all the rows making sure the value increases
                    // TODO: (equal value is not supported in rows) between rows
                    return true;
                case DECREASING:
                    // TODO: Check if field is a subfield or top level field.

                    // TODO: If field is top level field then fetch previous revision (save or approve operations can only modify the latest revision so if we check
                    // TODO: against the previous revision we should always be valid) then check if the current value is lower than (or equal to if we support value
                    // TODO: staying the same for multiple revisions) previous value.

                    // TODO: If field is a subfield field then fetch the immediate parent (if it exists) and go through all the rows making sure the value decreases
                    // TODO: (equal value is not supported in rows) between rows
                    return true;
                case EQUALS:
                    if(condition.getTarget() == null) {
                        // Condition must have a target for equality checking
                        return false;
                    }
                    switch(condition.getTarget().getType()) {
                        case FIELD:
                            // TODO: Validate against field value
                            return true;
                    }
                    return true;
                default:
                    return true;
            }
        }

        private boolean checkConditionForQuery(ResultList<? extends SearchResult> result, Condition condition) {
            switch(condition.getType()) {
                case IS_EMPTY:
                    return handleIsEmpty(result);
                case NOT_EMPTY:
                    return handleNotEmpty(result);
                case UNIQUE:
                    return handleUnique(result);
                default:
                    return true;
            }
        }

        // IS_EMPTY takes either a field or a search result and checks them for content
        private boolean handleIsEmpty(ResultList<? extends SearchResult> results) {
            return results.getResults().isEmpty();
        }

        // TODO: Currently returns true if there are rows for some language, should be possible to require specific language at some point.
        private boolean handleIsEmpty(ContainerDataField field) {
            return field == null || !field.hasRows();
        }

        private boolean handleIsEmpty(ReferenceContainerDataField field) {
            return field == null || field.getReferences().isEmpty();
        }

        // TODO: Currently checks all languages, should be possible at some point to restrict to specific language
        private boolean handleIsEmpty(ValueDataField field) {
            if(field == null) {
                return true;
            }
            boolean hasValue = false;
            for(Language l : Language.values()) {
                if(field.hasValueFor(l)) {
                    hasValue = true;
                    break;
                }
            }
            return !hasValue;
        }

        // NOT_EMPTY takes either a field or a search result and checks them for content
        private boolean handleNotEmpty(ResultList<? extends SearchResult> results) {
            return !results.getResults().isEmpty();
        }

        // TODO: Currently returns true if there are rows for some language, should be possible to require specific language at some point.
        private boolean handleNotEmpty(ContainerDataField field) {
            return field != null && field.hasRows();
        }

        private boolean handleNotEmpty(ReferenceContainerDataField field) {
            return field != null && !field.getReferences().isEmpty();
        }

        // TODO: Currently checks all languages, should be possible at some point to restrict to specific language
        private boolean handleNotEmpty(ValueDataField field) {
            if(field == null) {
                return false;
            }
            boolean hasValue = false;
            for(Language l : Language.values()) {
                if(field.hasValueFor(l)) {
                    hasValue = true;
                    break;
                }
            }
            return hasValue;
        }

        // UNIQUE
        private boolean handleUnique(ResultList<? extends SearchResult> results) {
            return results.getResults().size() == 1;
        }
    }
}
