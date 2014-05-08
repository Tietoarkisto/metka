package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.util.JSONUtil;
import fi.uta.fsd.metka.mvc.domain.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Interface for variables-object and variable-object reading and modification.
 * This controller handles both the browser-requests to view the variables page
 * as well as the ajax-requests for single variable operations.
 */
@Controller
@RequestMapping("/variables")
public class VariablesController {
    /*private static final String REDIRECT_SEARCH = "redirect:/study/search";
    private static final String REDIRECT_VIEW = "redirect:/study/view/";
    private static final String VIEW = "view";
    private static final String SEARCH = "search";
    private static final String MODIFY = "modify";*/

    @Autowired
    private ConfigurationService configService;
    @Autowired
    private JSONUtil json;

    /*
    * View variables
    * Use search functions to find relevant revision for the requested study variables. Then redirect to viewing that specific revision.
    * If no revision is found then return to search page with an error message.
    */
    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        /*Integer revision = studyService.findSingleRevisionNo(id);
        if(model.asMap().containsKey("displayableErrors")) {
            redirectAttributes.addFlashAttribute("displayableErrors", model.asMap().get("displayableErrors"));
        }
        if(revision != null) {
            return REDIRECT_VIEW+id+"/"+revision;
        } else {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noViewableRevision("study", id));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }*/
        return null;
    }

    /*
    * View single variables revision
    * Use search functions to find the requested variables revision.
    * Service will form a viewable object that can be added to the model.
    * If the returned single object is in DRAFT state and the current user is the handler
    * then show modify page, otherwise show view page.
    */
    @RequestMapping(value = "view/{id}/{revision}", method = RequestMethod.GET)
    public String viewRevision(Model model,
                               @PathVariable Integer id, @PathVariable Integer revision,
                               RedirectAttributes redirectAttributes) {
        /*TransferObject single = null;
        Configuration config = null;
        if(model.asMap().get("single") == null || model.asMap().get("studyconfiguration") == null) {
            RevisionViewDataContainer revData = studyService.findSingleRevision(id, revision);
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
            errors.add(ErrorMessage.noSuchRevision("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        if(config == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noConfigurationForRevision("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }

        Configuration fileConfig = configService.findLatestByType(ConfigurationType.STUDY_ATTACHMENT);
        if(fileConfig == null) {
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.noConfigurationForRevision("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        Map<String, Configuration> configurations = new HashMap<>();
        configurations.put(fileConfig.getKey().getType().toValue(), fileConfig);
        configurations.put(config.getKey().getType().toValue(), config);
        model.asMap().put("configuration", configurations);

        // Form JSConfig
        ConfigurationMap configs = new ConfigurationMap();
        configs.setConfiguration(config);
        configs.setConfiguration(fileConfig);
        try {
            model.asMap().put("jsConfig", json.serialize(configs));
        } catch(IOException ex) {
            ex.printStackTrace();
            List<ErrorMessage> errors = new ArrayList<>();
            errors.add(ErrorMessage.configurationSerializationError("study", id, revision));
            redirectAttributes.addFlashAttribute("displayableErrors", errors);
            return REDIRECT_SEARCH;
        }
        single.setUrlHash((String)model.asMap().get("urlHash"));
        model.asMap().put("page", "study");
        if(single.getState() == UIRevisionState.DRAFT) {
            // TODO: this should check if the user is the handler for this revision.
            return MODIFY;
        } else {
            return VIEW;
        }*/
        return null;
    }

    /*
    * Search for variables using query.
    * Variables-searches should always be exact, user knows for which study they want to view the variables for.
    * Perform a search for certain study id (including the letter part) and then find a variables object
    * from that study and return it.
    */
    @RequestMapping(value="search", method = {RequestMethod.GET, RequestMethod.POST})
    //public String search(Model model, @ModelAttribute("searchData")StudySearchData searchData) {
    public String search(Model model) {
        /*if(searchData.getQuery() != null) {
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
            errors.add(ErrorMessage.noResults("series"));
            model.asMap().put("displayableErrors", errors);
        }

        Configuration config = configService.findLatestByType(ConfigurationType.STUDY);
        Map<String, Configuration> configuration = new HashMap<>();
        configuration.put(config.getKey().getType().toValue(), config);
        model.asMap().put("configuration", configuration);

        model.asMap().put("page", "study");
        return SEARCH;*/
        return null;
    }

    /*
     * There is no add method for variable or variables object. These come strictly from parsers
     * (such as por-file parser) as a byproduct of the user uploading or otherwise introducing
     * a file containing variable data.
    */

    /*
    * Edit variables
    * Requests an editable revision for the variables. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    * IMPORTANT:
    * When variables-object is opened for editing then the study that contains those variables
    * also has to be opened to a DRAFT state (assuming it wasn't open before).
    */
    @RequestMapping(value = "edit/{id}", method = {RequestMethod.GET})
    public String edit(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        /*RevisionViewDataContainer revData = studyService.editStudy(id);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Notify user that no editable revision could be found or created
            return REDIRECT_VIEW+id;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("studyconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }*/
        return null;
    }

    /*
    * Save variables
    * Tell service to save given variables object. It will be validated and checked for changes
    * further along the line so on this point the assumption can be made that changes exist.
    * Return to the modify page after including the success status of the operation.
    */
    @RequestMapping(value="save", method = {RequestMethod.POST})
    //public String save(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
    public String save() {
        /*boolean success = studyService.saveStudy(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(success) {
            errors.add(ErrorMessage.saveSuccess());
        } else {
            errors.add(ErrorMessage.saveFail());
        }
        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        // TODO: IMPORTANT: If save failed user should not be redirected or the data should at least be the same they sent to server, otherwise users changes are lost.

        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision()+(single.getUrlHash() != null ? single.getUrlHash() : "");*/
        return null;
    }

    /*
     * Single variable or whole variables objects are never approved as is but always as part of approving
     * the study they are part of.
    */
}
