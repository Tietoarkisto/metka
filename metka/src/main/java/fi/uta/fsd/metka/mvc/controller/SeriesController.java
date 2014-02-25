package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.enums.ConfigurationType;
import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.mvc.domain.ConfigurationService;
import fi.uta.fsd.metka.mvc.domain.SeriesService;
import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;
import fi.uta.fsd.metka.mvc.domain.simple.RevisionViewDataContainer;
import fi.uta.fsd.metka.mvc.domain.simple.TransferObject;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchData;
import fi.uta.fsd.metka.mvc.domain.simple.series.SeriesSearchResultSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Handles all requests for series operations such as view and save.
 * All requests contain base address /series
 */
@Controller
@RequestMapping("/series")
public class SeriesController {

    private static final String REDIRECT_SEARCH = "redirect:/series/search";
    private static final String REDIRECT_VIEW = "redirect:/series/view/";
    private static final String VIEW = "series/view";
    private static final String SEARCH = "series/search";
    private static final String MODIFY = "series/modify";

    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ConfigurationService configService;

    /*
    * View single series
    * Use search functions to find relevant revision for the requested series. Then redirect to viewing that specific revision.
    * If no revision is found then return to search page with an error message.
    */
    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Integer revision = seriesService.findSingleRevisionNo(id);
        if(model.asMap().containsKey("errorContainer")) {
            redirectAttributes.addFlashAttribute("errorContainer", model.asMap().get("errorContainer"));
        }
        if(revision != null) {
            return REDIRECT_VIEW+id+"/"+revision;
        } else {
            redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.noViewableRevision("series", id));
            return REDIRECT_SEARCH;
        }
    }

    /*
    * View single series revision
    * Use search functions to find the requested series revision.
    * Service will form a viewable object that can be added to the model.
    * If the returned single object is in DRAFT state and the current user is the handler
    * then show modify page, otherwise show view page.
    */
    @RequestMapping(value = "view/{id}/{revision}", method = RequestMethod.GET)
    public String viewRevision(Model model,
                               @PathVariable Integer id, @PathVariable Integer revision,
                               RedirectAttributes redirectAttributes) {
        TransferObject single = null;

        if(model.asMap().get("single") == null || model.asMap().get("configuration") == null) {
            RevisionViewDataContainer revData = seriesService.findSingleRevision(id, revision);
            model.asMap().put("single", revData.getTransferObject());
            model.asMap().put("configuration", revData.getConfiguration());
            single = revData.getTransferObject();
        } else {
            single = (TransferObject)model.asMap().get("single");
        }

        if(single == null) {
            redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.noSuchRevision("series", id, revision));
            return REDIRECT_SEARCH;
        }

        model.asMap().put("page", "series");
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
    public String search(Model model, @ModelAttribute("searchData")SeriesSearchData searchData) {

        if(searchData.getQuery() != null) {
            List<SeriesSearchResultSO> results = seriesService.searchForSeries(searchData.getQuery());
            if(results.size() == 1) {
                return REDIRECT_VIEW+results.get(0).getSeriesno()+"/"+results.get(0).getRevision();
            }
            searchData.setResults(results);
            searchData.setQuery(searchData.getQuery());
        }

        searchData.setAbbreviations(seriesService.findAbbreviations());
        model.asMap().put("searchData", searchData);

        if(searchData.getQuery() != null && searchData.getResults().size() == 0) {
            model.asMap().put("errorContainer", ErrorMessage.noResults("series"));
        }

        Configuration config = configService.findLatestByType(ConfigurationType.SERIES);
        model.asMap().put("configuration", config);

        model.asMap().put("page", "series");
        return SEARCH;
    }

    /*
    * Add new series
    * Request a new series from the service then display MODIFY page for that series.
    * Only a DRAFT revision can be edited and only the newest revision can be a draft revision
    * so you can always modify by using only the id for the series.
    */
    @RequestMapping(value="add", method = {RequestMethod.GET})
    public String add(RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = seriesService.newSeries();
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Show error if no new series could be created
            return REDIRECT_SEARCH;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("configuration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }

    /*
    * Edit series
    * Requests an editable revision for the series. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    */
    @RequestMapping(value = "edit/{seriesno}", method = {RequestMethod.GET})
    public String edit(@PathVariable Integer seriesno, RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = seriesService.editSeries(seriesno);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Notify user that no editable revision could be found or created
            return REDIRECT_VIEW+seriesno;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("configuration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }

    /*
    * Save series
    * Tell service to save given series. It will be validated and checked for changes
    * further along the line so on this point the assumption can be made that changes exist.
    * Return to the modify page after including the success status of the operation.
    */
    @RequestMapping(value="save", method = {RequestMethod.POST})
    public String save(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = seriesService.saveSeries(single);

        if(success) {
            redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.saveSuccess());
        } else {
            redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.saveFail());
        }

        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision();
    }

    /*
    * Approve series
    * First makes sure that series is saved and if successful then requests series approval.
    * Since only DRAFTs can be approved and only the latest revision can be a DRAFT
    * only the series id is needed for the approval process. All required validation is done
    * later in the approval process.
    */
    @RequestMapping(value="approve", method = {RequestMethod.POST})
    public String approve(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = seriesService.saveSeries(single);

        if(!success) {
            redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.approveFailSave());
        } else {
            success = seriesService.approveSeries(single);

            if(!success) {
                redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.approveFailValidate());
            } else {
                redirectAttributes.addFlashAttribute("errorContainer", ErrorMessage.approveSuccess());
            }
        }

        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision();
    }
}
