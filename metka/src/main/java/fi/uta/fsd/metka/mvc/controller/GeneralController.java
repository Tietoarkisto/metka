package fi.uta.fsd.metka.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Includes controllers for general functionality that doesn't warrant its own controller or doesn't fit anywhere else
 */
@Controller
public class GeneralController {

    // Doesn't work as expected
    /*@RequestMapping(value = "", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/web/expert";
    }*/

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAllSlash() {
        return "redirect:/web/expert";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        /*request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();

        return "redirect:https://"+request.getServerName()+"/Shibboleth.sso/Logout";*/
        return "redirect:/j_spring_security_logout";
    }
}
