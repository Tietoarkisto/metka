package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.ExpertSearchService;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchListResponse;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryRequest;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.expert.SavedExpertSearchItem;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("expert")
public class ExpertSearchController {
    @Autowired
    private ExpertSearchService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String expert(Model model) {
        ModelUtil.initExpertSearch(model);
        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value = "list", method = {RequestMethod.GET})
    public @ResponseBody ExpertSearchListResponse listSavedExpertSearches() {
        return service.listSavedSearcher();
    }

    @RequestMapping(value = "query", method = {RequestMethod.POST})
    public @ResponseBody ExpertSearchQueryResponse expertSearchQuery(@RequestBody ExpertSearchQueryRequest request) {
        return service.performQuery(request);
    }

    @RequestMapping(value = "remove/{id}", method = {RequestMethod.GET})
    public @ResponseBody Boolean removeExpertSearch(@PathVariable Long id) {
        service.removeExpertSearch(id);
        return true;
    }

    @RequestMapping(value = "save", method = {RequestMethod.POST})
    public @ResponseBody SavedExpertSearchItem saveExpertSearch(@RequestBody SavedExpertSearchItem item) {
        return service.saveExpertSearch(item);
    }
}
