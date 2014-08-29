package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.BinderService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderListResponse;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("binder")
public class BinderController {
    @Autowired
    private BinderService service;

    @RequestMapping("")
    public String settings(Model model) {
        ModelUtil.initBinder(model);
        return AuthenticationUtil.getModelName("revision", model);
    }

    @RequestMapping(value="saveBinderPage", method = RequestMethod.POST)
    public @ResponseBody BinderListResponse saveBinderPage(@RequestBody SaveBinderPageRequest request) {
        return service.saveBinderPage(request);
    }

    @RequestMapping(value = "removePage/{pageId}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removePage(@PathVariable Long pageId) {
        return service.removePage(pageId);
    }

    @RequestMapping(value="listStudyBinderPages/{id}", method = RequestMethod.GET)
    public @ResponseBody
    BinderListResponse listStudyBinderPages(@PathVariable Long id) {
        return service.listStudyBinderPages(id);
    }

    @RequestMapping(value="listBinderPages", method = RequestMethod.GET)
    public @ResponseBody
    BinderListResponse listBinderPages() {
        return service.listBinderPages();
    }

    @RequestMapping(value="binderContent/{binderId}", method = RequestMethod.POST)
    public @ResponseBody
    BinderListResponse binderContent(@PathVariable Long binderId) {
        return service.binderContent(binderId);
    }
}
