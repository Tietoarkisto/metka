package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/21/13
 * Time: 10:01 AM
 */
@Controller("settingsController")
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private DomainFacade domain;

    @RequestMapping(value = "user", method= RequestMethod.GET)
    public String showUser(@PathVariable String userId, Model model) {
        model.addAttribute("page", "settings");
        model.addAttribute("sub", "user");

        return "settings";
    }
}
