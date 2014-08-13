package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ConfigurationService;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.StudyErrorService;
import fi.uta.fsd.metka.mvc.services.StudyService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.study.StudyError;
import fi.uta.fsd.metka.transfer.study.StudyErrorListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Handles all requests for study operations such as view and save.
 * All requests contain base address /study
 */
@Controller
@RequestMapping("/study")
public class StudyController {
    private static final String REDIRECT_SEARCH = "redirect:/study/search";
    private static final String REDIRECT_VIEW = "redirect:/study/view/";
    private static final String VIEW = "view";
    private static final String SEARCH = "search";
    private static final String MODIFY = "modify";

    @Autowired
    private StudyService studyService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private JSONUtil json;
    @Autowired
    private GeneralService general;

    @Autowired
    private StudyErrorService errors;

    // TODO: Add study specific ajax calls like binder list, errors list and error operations (add, modify, remove)
    @RequestMapping(value="listErrors/{id}/{no}", method = RequestMethod.GET)
    public @ResponseBody StudyErrorListResponse listStudyErrors(@PathVariable Long id, @PathVariable Integer no) {
        return errors.getStudyErrorList(id, no);
    }

    @RequestMapping(value="updateError", method = RequestMethod.POST)
    public @ResponseBody ReturnResult updateStudyError(@RequestBody StudyError error) {
        return errors.insertOrUpdateStudyError(error);
    }

    @RequestMapping(value = "removeError/{id}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removeStudyError(@PathVariable Long id) {
        return errors.removeStudyError(id);
    }



    /*
    * View single study
    * Use search functions to find relevant revision for the requested study. Then redirect to viewing that specific revision.
    * If no revision is found then return to search page with an error message.
    */
    /*@RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Pair<ReturnResult, Integer> revision = studyService.findSingleRevisionNo(id);
        if(model.asMap().containsKey("displayableErrors")) {
            redirectAttributes.addFlashAttribute("displayableErrors", model.asMap().get("displayableErrors"));
        }
        if(revision.getLeft() == ReturnResult.REVISION_FOUND) {
            return REDIRECT_VIEW+id+"/"+revision.getRight();
        } else {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noViewableRevision("study", id));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
    }*/

    /*
    * View single study
    * Use search functions to find the requested study revision.
    * Service will form a viewable object that can be added to the model.
    * If the returned single object is in DRAFT state and the current user is the handler
    * then show modify page, otherwise show view page.
    */
    /*@RequestMapping(value = "view/{id}/{no}", method = RequestMethod.GET)
    public String viewRevision(Model model,
                               @PathVariable Long id, @PathVariable Integer no,
                               RedirectAttributes redirectAttributes) {
        TransferObject single = null;
        Configuration config = null;
        if(model.asMap().get("single") == null || model.asMap().get("studyconfiguration") == null) {
            RevisionViewDataContainer revData = studyService.findSingleRevision(id, no);
            if(revData != null) {
                model.asMap().put("single", revData.getTransferObject());
                //model.asMap().put("configuration", revData.getConfiguration());
                single = revData.getTransferObject();
                config = revData.getConfiguration();
            }
        } else {
            single = (TransferObject)model.asMap().get("single");
            config = (Configuration)model.asMap().get("studyconfiguration");
        }

        if(single == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noSuchRevision("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        if(config == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noConfigurationForRevision("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }

        Pair<ReturnResult, Configuration> configPair = configService.findLatestByType(ConfigurationType.STUDY_ATTACHMENT);
        if(configPair.getLeft() != ReturnResult.CONFIGURATION_FOUND) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noConfigurationForRevision("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        Configuration fileConfig = configPair.getRight();
        Map<String, Configuration> configurations = new HashMap<>();
        configurations.put(fileConfig.getKey().getType().toValue(), fileConfig);
        configurations.put(config.getKey().getType().toValue(), config);
        model.asMap().put("configuration", configurations);

        // Form JSConfig
        ConfigurationMap configs = new ConfigurationMap();
        configs.setConfiguration(config);
        configs.setConfiguration(fileConfig);
        Pair<ReturnResult, String> string = json.serialize(configs);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.configurationSerializationError("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsConfig", string.getRight());
        }

        // Form JSGUIConfig
        GUIConfigurationMap guiConfigs = new GUIConfigurationMap();
        Pair<ReturnResult, GUIConfiguration> guiPair = configService.findLatestGUIByType(ConfigurationType.STUDY);
        if(guiPair.getLeft() == ReturnResult.CONFIGURATION_FOUND) {
            guiConfigs.setConfiguration(guiPair.getRight());
        }

        string = json.serialize(guiConfigs);
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.guiConfigurationSerializationError("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsGUIConfig", string.getRight());
        }

        // Data
        Pair<ReturnResult, RevisionData> pair = general.getRevisionData(id, no, ConfigurationType.STUDY);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.guiConfigurationSerializationError("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        string = json.serialize(pair.getRight());
        if(string.getLeft() != ReturnResult.SERIALIZATION_SUCCESS) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.guiConfigurationSerializationError("study", id, no));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        } else {
            model.asMap().put("jsData", string.getRight());
        }

        single.setUrlHash((String)model.asMap().get("urlHash"));
        model.asMap().put("page", "study");
        if(single.getState() == UIRevisionState.DRAFT) {
            // TODO: this should check if the user is the handler for this revision.
            return MODIFY;
        } else {
            return VIEW;
        }
    }*/

    /*
    * Search for study using query
    * Use search functions to get a list of studies matching the user defined query.
    * If there is only one result then redirect to view.
    * Otherwise show search page with the result in the model.
    */
    /*@RequestMapping(value="search", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(Model model, @ModelAttribute("searchData")StudySearchData searchData) {
        if(searchData.getQuery() != null) {
            List<SearchResult> results = studyService.searchForStudies(searchData.getQuery());
            if(results.size() == 1) {
                return REDIRECT_VIEW+results.get(0).getId()+"/"+results.get(0).getRevision();
            }
            searchData.setResults(results);
            searchData.setQuery(searchData.getQuery());
        }

        model.asMap().put("searchData", searchData);

        if(searchData.getQuery() != null && searchData.getResults().size() == 0) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noResults("study"));
            model.asMap().put("displayableErrors", errors);
        }

        Pair<ReturnResult, Configuration> configPair = configService.findLatestByType(ConfigurationType.STUDY);
        Map<String, Configuration> configuration = new HashMap<>();
        if(configPair.getLeft() == ReturnResult.CONFIGURATION_FOUND) {
            configuration.put(ConfigurationType.STUDY.toValue(), configPair.getRight());
        }
        model.asMap().put("configuration", configuration);

        model.asMap().put("page", "study");
        return SEARCH;
    }*/

    /*
    * Add new study
    * Request a new study from the service then display MODIFY page for that study.
    * Only a DRAFT revision can be edited and only the newest revision can be a draft revision
    * so you can always modify by using only the id for the series.
    */
    /*@RequestMapping(value="add/{acquisition_number}", method = {RequestMethod.GET})
    public String add(@PathVariable Long acquisition_number, RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = studyService.newStudy(acquisition_number);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Show error if no new study could be created
            return REDIRECT_SEARCH;
        } else {

            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("studyconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }*/

    /*
    * Edit study
    * Requests an editable revision for the study. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    * TODO: Change to ajax request
    */
    /*@RequestMapping(value = "edit/{id}", method = {RequestMethod.GET})
    public String edit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = studyService.editStudy(id);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Notify user that no editable revision could be found or created
            return REDIRECT_VIEW+id;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("studyconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }*/

    /*
    * Save study
    * Tell service to save given study. It will be validated and checked for changes
    * further along the line so on this point the assumption can be made that changes exist.
    * Return to the modify page after including the success status of the operation.
    */
    /*@RequestMapping(value="save", method = {RequestMethod.POST})
    public String save(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = studyService.saveStudy(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(success) {
            errors.add(ErrorMessage.saveSuccess());
        } else {
            errors.add(ErrorMessage.saveFail());
        }
        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        // TODO: IMPORTANT: If save failed user should not be redirected or the data should at least be the same they sent to server, otherwise users changes are lost.

        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision()+(single.getUrlHash() != null ? single.getUrlHash() : "");
    }*/

    /*
    * Approve study
    * First makes sure that study is saved and if successful then requests study approval.
    * Since only DRAFTs can be approved and only the latest revision can be a DRAFT
    * only the study id is needed for the approval process. All required validation is done
    * later in the approval process.
    */
    /*@RequestMapping(value="approve", method = {RequestMethod.POST})
    public String approve(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = studyService.saveStudy(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(!success) {
            errors.add(ErrorMessage.approveFailSave());
        } else {
            success = studyService.approveStudy(single);

            if(!success) {
                errors.add(ErrorMessage.approveFailValidate());
            } else {
                errors.add(ErrorMessage.approveSuccess());
            }
        }
        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        // Set urlHash so the user can be directed back to the tab they were on.
        if(single.getUrlHash() != null) {
            redirectAttributes.addFlashAttribute("urlHash", single.getUrlHash());
        }
        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision()+(single.getUrlHash() != null ? single.getUrlHash() : "");
    }*/
}
