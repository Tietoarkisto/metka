package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/21/13
 * Time: 1:18 PM
 */
@Controller("dialogController")
@RequestMapping("/dialogs")
public class DialogController {

    @RequestMapping(value = "compareVersionsDialog", method = RequestMethod.GET)
    public String compareVersionsDialog() {
        return "compareVersionsDialog";
    }
}
