package fi.uta.fsd.metka.storage.restrictions;

import fi.uta.fsd.Logger;
import fi.uta.fsd.metka.model.configuration.Check;
import fi.uta.fsd.metka.model.configuration.Condition;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.configuration.Target;
import fi.uta.fsd.metka.model.interfaces.DataFieldContainer;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ListBasedResultList;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.SearchResult;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

class QueryTargetHandler {

    static boolean handle(Target target, DataFieldContainer context, DataFieldValidator validator, Configuration configuration, SearcherComponent searcher) {
        ResultList<? extends SearchResult> result = performQuery(target.getContent(), searcher, context, configuration);
        for(Check check : target.getChecks()) {
            // Check is enabled
            if(validator.validate(check.getRestrictors(), context, configuration)) {
                if(!checkCondition(result, check.getCondition())) {
                    return false;
                }
            }
        }
        return validator.validate(target.getTargets(), context, configuration);
    }

    private static ResultList<? extends SearchResult> performQuery(String query, SearcherComponent searcher, DataFieldContainer context, Configuration configuration) {
        query = query.replace("{id}", context.getRevisionKey().getId().toString());
        try {
            return searcher.executeSearch(ExpertRevisionSearchCommand.build(query, configuration));
        } catch (QueryNodeException qne) {
            Logger.error(QueryTargetHandler.class, "Exception while performing query: " + query);
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
