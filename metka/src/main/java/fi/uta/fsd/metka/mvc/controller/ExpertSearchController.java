package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/21/13
 * Time: 1:16 PM
 */
@Controller("expertSearchController")
@RequestMapping("/expertSearch")
public class ExpertSearchController {

    @RequestMapping(method = RequestMethod.GET)
    public String basicHandler() {
        return "expertSearch";
    }
}
