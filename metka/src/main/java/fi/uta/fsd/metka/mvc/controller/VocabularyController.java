package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.entity.VocabularyEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/19/13
 * Time: 2:04 PM
 */
@Controller("vocabularyController")
@RequestMapping("/vocabulary")
public class VocabularyController {
    @ModelAttribute("Vocabulary")
    public VocabularyEntity getVocabularyEntity() {
        return new VocabularyEntity();
    }

    @Autowired
    private DomainFacade domain;

    @RequestMapping("add")
    public String addVocabulary(@ModelAttribute("Vocabulary")VocabularyEntity vocabulary, BindingResult result) {
        domain.createVocabulary(vocabulary);
        return "redirect:/";
    }

    @RequestMapping("remove/{vocabularyId}")
    public String removeVocabulary(@PathVariable String vocabularyId) {
        domain.removeVocabulary(vocabularyId);
        return "redirect:/";
    }
}
