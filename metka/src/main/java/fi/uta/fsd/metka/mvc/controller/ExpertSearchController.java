package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchListResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/expertSearch")
public class ExpertSearchController {
    @Autowired
    private ExpertSearchService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String expertSearch(Model model) {
        model.asMap().put("configurationType", "EXPERTSEARCH");
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

    @RequestMapping(value = "remove/{id}", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean removeExpertSearch(@PathVariable Long id) {
        service.removeExpertSearch(id);
        return true;
    }
}
