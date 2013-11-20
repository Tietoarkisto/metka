package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 11/20/13
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller("desktopController")
@RequestMapping("/desktop")
public class DesktopController {

    @RequestMapping(method = RequestMethod.GET)
    public String basicHandler(Model model) {
        model.addAttribute("page", "desktop");

        return "desktop";
    }
}
