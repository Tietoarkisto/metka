package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/19/13
 * Time: 2:25 PM
 */
@Controller("standardTextController")
@RequestMapping("/standardText")
public class StandardTextController {

    /*@ModelAttribute("Vocabulary")
    public StandardTextEntity getVocabularyEntity() {
        return new StandardTextEntity();
    }*/

    @Autowired
    private DomainFacade domain;
}
