package fi.uta.fsd.metkaSearch.searchers;

import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;

public interface SearchCommandHandler {
    /**
     * Executes given search command and returns a ResultList containing all relevant results for the search.
     * Actual implementation of the execution will differ greatly between searchers from simple one field search
     * to multiple field boolean search or even multiple sequential searches for CONTAINER data.
     * @return ResultList Contains all relevant results from executing the SearchCommand based on the implementation of the commands ResultHandler
     */
    public ResultList execute(SearchCommand command);
}
