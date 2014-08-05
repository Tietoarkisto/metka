package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ConfigurationService;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.SeriesService;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles all requests for series operations such as view and save.
 * All requests contain base address /series
 */
@Controller
@RequestMapping("/series")
public class SeriesController {

    private static final String REDIRECT_SEARCH = "redirect:/series/search";
    private static final String REDIRECT_VIEW = "redirect:/series/view/";
    private static final String VIEW = "view";
    private static final String SEARCH = "search";
    private static final String MODIFY = "modify";

    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private JSONUtil json;
    @Autowired
    private GeneralService general;

    /*
    * View single series
    * Use search functions to find relevant revision for the requested series. Then redirect to viewing that specific revision.
    * If no revision is found then return to search page with an error message.
    */
    /*@RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Pair<ReturnResult, Integer> revision = seriesService.findSingleRevisionNo(id);
        if(model.asMap().containsKey("displayableErrors")) {
            redirectAttributes.addFlashAttribute("displayableErrors", model.asMap().get("displayableErrors"));
        }
        if(revision.getLeft() == ReturnResult.REVISION_FOUND) {
            return REDIRECT_VIEW+id+"/"+revision.getRight();
        } else {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noViewableRevision("series", id));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
    }*/

    /*
    * View single series revision
    * Use search functions to find the requested series revision.
    * Service will form a viewable object that can be added to the model.
    * If the returned single object is in DRAFT state and the current user is the handler
    * then show modify page, otherwise show view page.
    */
    /*@RequestMapping(value = "view/{id}/{revision}", method = RequestMethod.GET)
    public String viewRevision(Model model,
                               @PathVariable Long id, @PathVariable Integer revision,
                               RedirectAttributes redirectAttributes) {
        TransferData transferData = null;
        Configuration config = null;
        if(model.asMap().get("single") == null || model.asMap().get("seriesconfiguration") == null) {
            RevisionViewDataContainer revData = seriesService.findSingleRevision(id, revision);
            if(revData != null) {
                model.asMap().put("single", revData.getTransferObject());
                Map<String, Configuration> configuration = new HashMap<>();
                configuration.put("SERIES", revData.getConfiguration());
                model.asMap().put("configuration", configuration);
                transferData = revData.getTransferObject();
                config = revData.getConfiguration();
            }
        } else {
            transferData = (TransferData)model.asMap().get("single");
            config = (Configuration)model.asMap().get("seriesconfiguration");
        }

        if(transferData == null || config == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noSuchRevision("series", id, revision));
            errors.add(ErrorMessage.noSuchRevision("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }

        // Form JSConfig
        ConfigurationMap configs = new ConfigurationMap();
        configs.setConfiguration(config);
        //configs.setConfiguration(fileConfig);
        String result;

        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        result = json.serialize(configs).getRight();
        if(result == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.configurationSerializationError("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsConfig", result);
        }

        // Form JSGUIConfig
        GUIConfigurationMap guiConfigs = new GUIConfigurationMap();
        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        GUIConfiguration guiConfig = configService.findLatestGUIByType(ConfigurationType.SERIES).getRight();
        guiConfigs.setConfiguration(guiConfig);

        // TODO: Just skip checks for now, if this raises a problem at some point then do complete checks
        result = json.serialize(guiConfigs).getRight();
        if(result == null) {

            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.guiConfigurationSerializationError("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsGUIConfig", result);
        }

        // Data
        Pair<ReturnResult, RevisionData> pair = general.getRevisionData(id, revision, ConfigurationType.SERIES);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.guiConfigurationSerializationError("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsData", json.serialize(pair.getRight()).getRight());
        }

        model.asMap().put("page", "series");
        Map<String, Configuration> configurations = new HashMap<>();
        configurations.put("SERIES", config);
        model.asMap().put("configuration", configurations);
        model.asMap().put("single", single);

        return VIEW;
    }*/

    /*
    * Add new series
    * Request a new series from the service then display MODIFY page for that series.
    * Only a DRAFT revision can be edited and only the newest revision can be a draft revision
    * so you can always modify by using only the id for the series.
    */
    /*@RequestMapping(value="add", method = {RequestMethod.GET})
    public String add(RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = seriesService.newSeries();
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Show error if no new series could be created
            return REDIRECT_SEARCH;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("seriesconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }*/

    /*
    * Edit series
    * Requests an editable revision for the series. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    * TODO: Change to ajax request
    */
    /*@RequestMapping(value = "edit/{seriesno}", method = {RequestMethod.GET})
    public String edit(@PathVariable Long seriesno, RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = seriesService.editSeries(seriesno);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Notify user that no editable revision could be found or created
            return REDIRECT_VIEW+seriesno;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("seriesconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }*/

    /*
    * Save series
    * Tell service to save given series. It will be validated and checked for changes
    * further along the line so on this point the assumption can be made that changes exist.
    * Return to the modify page after including the success status of the operation.
    */
    /*@RequestMapping(value="save", method = {RequestMethod.POST})
    public String save(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = seriesService.saveSeries(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(success) {
            errors.add(ErrorMessage.saveSuccess());
        } else {
            errors.add(ErrorMessage.saveFail());
        }

        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision();
    }*/

    /*
    * Approve series
    * First makes sure that series is saved and if successful then requests series approval.
    * Since only DRAFTs can be approved and only the latest revision can be a DRAFT
    * only the series id is needed for the approval process. All required validation is done
    * later in the approval process.
    */
    /*@RequestMapping(value="approve", method = {RequestMethod.POST})
    public String approve(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = seriesService.saveSeries(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(!success) {
            errors.add(ErrorMessage.approveFailSave());
        } else {
            success = seriesService.approveSeries(single);

            if(!success) {
                errors.add(ErrorMessage.approveFailValidate());
            } else {
                errors.add(ErrorMessage.approveSuccess());
            }
        }

        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision();
    }*/
}
