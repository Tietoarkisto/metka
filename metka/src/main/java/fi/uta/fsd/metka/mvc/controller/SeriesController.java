package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.mvc.domain.ConfigurationService;
import fi.uta.fsd.metka.mvc.domain.SeriesService;
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

    private static final String VIEW = "series/view";
    private static final String SEARCH = "series/search";
    private static final String MODIFY = "series/modify";

    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ConfigurationService configurationService;

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @ModelAttribute("info")SeriesInfo info, @PathVariable Integer id) {
        model.addAttribute("page", "series");

        SeriesSearchSO query = new SeriesSearchSO();
        query.setId(id);
        SeriesSingleSO single = seriesService.findSingleSeries(query);

        info.setSingle(single);
        model.addAttribute("info", info);
        if(single.getState() == RevisionState.DRAFT) {
            // TODO: this should check if the user is the handler for this revision.
            return MODIFY;
        } else {
            return VIEW;
        }
    }

    @RequestMapping(value="search", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        info.setAbbreviations(seriesService.findAbbreviations());
        List<SeriesSearchSO> results = seriesService.searchForSeries(info.getQuery());
        if(results.size() == 1) {
            return "redirect:/series/view/"+results.get(0).getId();
        }
        info.setResults(results);
        info.setQuery(info.getQuery());

        model.addAttribute("info", info);

        return SEARCH;
    }

    @RequestMapping(value="add", method = {RequestMethod.GET})
    public String add(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        SeriesSingleSO single = seriesService.newSeries();
        // TODO: Show error if no new series could be created
        if(single == null) {
            return SEARCH;
        }
        info.setSingle(single);


        return MODIFY;
    }

    @RequestMapping(value="save", method = {RequestMethod.POST})
    public String save(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        boolean success = seriesService.saveSeries(info.getSingle());
        model.addAttribute("success", success);

        return MODIFY;
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
