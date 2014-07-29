package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.mvc.services.ConfigurationService;
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
    public String view(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {

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
                               @PathVariable Long id, @PathVariable Integer revision,
                               RedirectAttributes redirectAttributes) {

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
    public String edit(@PathVariable Long id, RedirectAttributes redirectAttributes) {

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

        return null;
    }

    /*
     * Single variable or whole variables objects are never approved as is but always as part of approving
     * the study they are part of.
    */
}
