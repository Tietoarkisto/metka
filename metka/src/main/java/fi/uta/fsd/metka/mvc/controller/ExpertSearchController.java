package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.ExpertSearchService;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchListResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaSearch.SearcherComponent;
import fi.uta.fsd.metkaSearch.commands.searcher.SearchCommand;
import fi.uta.fsd.metkaSearch.commands.searcher.expert.ExpertRevisionSearchCommand;
import fi.uta.fsd.metkaSearch.results.ResultList;
import fi.uta.fsd.metkaSearch.results.RevisionResult;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/expertSearch")
public class ExpertSearchController {
    @Autowired
    private ExpertSearchService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String expertSearch() {
        return "expertSearch";
    }

    @RequestMapping(value = "query", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ExpertSearchQueryResponse expertSearchQuery(@RequestBody ExpertSearchQueryRequest request) {
        return service.performQuery(request);
    }

    @RequestMapping(value = "list", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ExpertSearchListResponse listSavedExpertSearches() {
        return service.listSavedSearcher();
    }

    @RequestMapping(value = "save", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody SavedExpertSearchItem saveExpertSearch(@RequestBody SavedExpertSearchItem item) {
        return service.saveExpertSearch(item);
    }
}
