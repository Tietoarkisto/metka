package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.GeneralService;
import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/10/14
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class GeneralController {

    @Autowired
    private GeneralService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/series/search";
    }

    @RequestMapping(value = "/prev/{type}/{id}", method = RequestMethod.GET)
    public String prev(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, false);
        } catch(NotFoundException e) {
            ErrorMessage error = new ErrorMessage();
            error.setTitle("general.errors.title.notice");
            error.setMsg("general.errors.move.previous");
            error.getData().add("general.errors.move."+type);

            redirectAttributes.addFlashAttribute("errorContainer", error);
        }
        return "redirect:/"+type+"/view/"+id;

    }

    @RequestMapping(value = "/next/{type}/{id}", method = RequestMethod.GET)
    public String next(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, true);
        } catch(NotFoundException e) {
            ErrorMessage error = new ErrorMessage();
            error.setTitle("general.errors.title.notice");
            error.setMsg("general.errors.move.next");
            error.getData().add("general.errors.move."+type);

            redirectAttributes.addFlashAttribute("errorContainer", error);
        }
        return "redirect:/"+type+"/view/"+id;
    }
}
