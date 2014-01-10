package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/10/14
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class GeneralController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/series/search";
    }
}
