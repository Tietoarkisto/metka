package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.metka.model.configuration.Check;
import fi.uta.fsd.metka.model.configuration.Condition;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class QueryTargetHandler {
    private static final Logger logger = LoggerFactory.getLogger(QueryTargetHandler.class);

    static boolean handle(Target target, RevisionData revision, RestrictionValidator validator, SearcherComponent searcher, Configuration configuration) {
        ResultList<? extends SearchResult> result = performQuery(target.getContent(), searcher, revision, configuration);
        for(Check check : target.getChecks()) {
            // Check is enabled
            if(validator.validate(revision, check.getRestrictors())) {
                if(!checkCondition(result, check.getCondition())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static ResultList<? extends SearchResult> performQuery(String query, SearcherComponent searcher, RevisionData revision, Configuration configuration) {
        query = query.replace("{id}", revision.getKey().getId().toString());
        try {
            return searcher.executeSearch(ExpertRevisionSearchCommand.build(query, configuration));
        } catch (QueryNodeException qne) {
            logger.error("Exception while performing query: "+query);
            return new ListBasedResultList<>(ResultList.ResultType.REVISION);
        }
    }

    private static boolean checkCondition(ResultList<? extends SearchResult> result, Condition condition) {
        switch(condition.getType()) {
            case NOT_EMPTY:
                return notEmpty(result);
            case IS_EMPTY:
                return isEmpty(result);
            case UNIQUE:
                return unique(result);
            default:
                return true;
        }
    }

    private static boolean notEmpty(ResultList<? extends SearchResult> results) {
        return !results.getResults().isEmpty();
    }

    private static boolean isEmpty(ResultList<? extends SearchResult> results) {
        return results.getResults().isEmpty();
    }

    private static boolean unique(ResultList<? extends SearchResult> results) {
        return results.getResults().size() == 1;
    }
}
