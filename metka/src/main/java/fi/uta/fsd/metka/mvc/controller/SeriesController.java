package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.SeriesFacade;
import fi.uta.fsd.metka.mvc.domain.simple.SeriesInfo;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchSO;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSingleSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/19/13
 * Time: 1:51 PM
 */
@Controller("seriesController")
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesFacade facade;

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewSeries(Model model, @PathVariable Integer id) {
        model.addAttribute("page", "series");

        SeriesInfo info = new SeriesInfo();

        SeriesSearchSO query = new SeriesSearchSO();
        query.setId(id);
        SeriesSingleSO single = facade.findSeries(query);

        info.setSingle(single);
        model.addAttribute("info", info);

        return "series/view";
    }

    @RequestMapping(value="search", method = {RequestMethod.GET, RequestMethod.POST})
    public String basicHandler(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        System.err.println(info.getQuery());

        info.setAbbreviations(facade.findAbbreviations());
        info.setResults(facade.searchForSeries(info.getQuery()));
        info.setQuery(info.getQuery());

        model.addAttribute("info", info);

        return "series/search";
    }

    /*@RequestMapping(value = "add", method = RequestMethod.POST)
    public String addSeries(@ModelAttribute("Series")SeriesEntity series, BindingResult result) {
        domain.createSeries(series);
        return "redirect:/";
    }

    @RequestMapping(value = "remove/{seriesId}")
    public String removeSeries(@PathVariable Integer seriesId) {
        domain.removeSeries(seriesId);
        return "redirect:/";
    }*/
}
