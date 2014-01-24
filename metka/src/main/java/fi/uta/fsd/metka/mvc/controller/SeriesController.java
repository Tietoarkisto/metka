package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.mvc.domain.ConfigurationService;
import fi.uta.fsd.metka.mvc.domain.SeriesService;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesInfo;
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

    /*
    * View single series
    * Use search functions to find the requested series.
    * Service will form a viewable object that can be added to the model.
    * If the returned single object is in DRAFT state then show modify page,
    * otherwise show view page.
    */
    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @ModelAttribute("info")SeriesInfo info, @PathVariable Integer id) {
        model.asMap().put("page", "series");

        SeriesSingleSO single = seriesService.findSingleSeries(id);

        info.setSingle(single);
        model.addAttribute("info", info);
        if(single.getState() == RevisionState.DRAFT) {
            // TODO: this should check if the user is the handler for this revision.
            return MODIFY;
        } else {
            return VIEW;
        }
    }

    /*
    * Search for series using query
    * Use search functions to get a list of series matching the user defined query.
    * If there is only one result then redirect to view.
    * Otherwise show search page with the result in the model.
    */
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

    /*
    * Add new series
    * Request a new series from the service then display MODIFY page for that series.
    * Only a DRAFT revision can be edited and only the newest revision can be a draft revision
    * so you can always modify by using only the id for the series.
    */
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

    /*
    * Save series
    * Tell service to save given series. It will be validated and checked for changes
    * further along the line so on this point the assumption can be made that changes exist.
    * Return to the modify page after including the success status of the operation.
    */
    @RequestMapping(value="save", method = {RequestMethod.POST})
    public String save(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        boolean success = seriesService.saveSeries(info.getSingle());
        model.addAttribute("saveFail", !success);

        return MODIFY;
    }

    /*
    * Approve series
    * First makes sure that series is saved and if successful then requests series approval.
    * Since only DRAFTs can be approved and only the latest revision can be a DRAFT
    * only the series id is needed for the approval process. All required validation is done
    * later in the approval process.
    */
    @RequestMapping(value="approve", method = {RequestMethod.POST})
    public String approve(Model model, @ModelAttribute("info")SeriesInfo info, BindingResult result) {
        model.addAttribute("page", "series");

        boolean success = seriesService.saveSeries(info.getSingle());

        if(!success) {
            model.addAttribute("saveFail", !success);
            return MODIFY;
        }

        success = seriesService.approveSeries(info.getSingle().getId());

        if(!success) {
            model.addAttribute("approveFail", !success);
            return MODIFY;
        }

        return "redirect:/series/view/"+info.getSingle().getId();
    }

    /*
    * Edit series
    * Requests an editable revision for the series. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    */
    @RequestMapping(value = "edit/{id}", method = {RequestMethod.GET})
    public String edit(Model model, @ModelAttribute("info")SeriesInfo info, @PathVariable Integer id) {
        model.addAttribute("page", "series");

        SeriesSingleSO single = seriesService.editSeries(id);
        if(single != null) {
            info.setSingle(single);
            return MODIFY;
        } else {
            // TODO: Notify user that no editable revision could be found
            return "redirect:/series/view/"+id;
        }
    }
}
