package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.deprecated.SeriesEntity;
import fi.uta.fsd.metka.mvc.domain.DomainFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/19/13
 * Time: 1:51 PM
 */
@Controller("seriesController")
@RequestMapping("/series")
public class SeriesController {

    @ModelAttribute("Series")
    public SeriesEntity getSeriesEntity() {
        return new SeriesEntity();
    }

    @Autowired
    private DomainFacade domain;

    @RequestMapping(value="search", method = RequestMethod.GET)
    public String basicHandler(Model model, @ModelAttribute("Series")SeriesEntity series, BindingResult result) {
        model.addAttribute("page", "series");

        List<String> abbs = domain.listAllSeriesAbbreviations();
        model.addAttribute("abbreviations", abbs);

        if(series.getId() != null || series.getName() != null || series.getAbbreviation() != null) {

        }

        return "seriesSearch";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addSeries(@ModelAttribute("Series")SeriesEntity series, BindingResult result) {
        domain.createSeries(series);
        return "redirect:/";
    }

    @RequestMapping(value = "remove/{seriesId}")
    public String removeSeries(@PathVariable Integer seriesId) {
        domain.removeSeries(seriesId);
        return "redirect:/";
    }
}
