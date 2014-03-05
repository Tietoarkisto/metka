package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.data.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.data.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.mvc.domain.GeneralService;
import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/10/14
 * Time: 10:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class GeneralController {

    @Autowired
    private GeneralService service;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/series/search";
    }

    @RequestMapping(value = "/prev/{type}/{id}", method = RequestMethod.GET)
    public String prev(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, false);
        } catch(NotFoundException e) {
            ErrorMessage error = new ErrorMessage();
            error.setTitle("general.errors.title.notice");
            error.setMsg("general.errors.move.previous");
            error.getData().add("general.errors.move." + type);

            redirectAttributes.addFlashAttribute("errorContainer", error);
        }
        return "redirect:/"+type+"/view/"+id;

    }

    @RequestMapping(value = "/next/{type}/{id}", method = RequestMethod.GET)
    public String next(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, true);
        } catch(NotFoundException e) {
            ErrorMessage error = new ErrorMessage();
            error.setTitle("general.errors.title.notice");
            error.setMsg("general.errors.move.next");
            error.getData().add("general.errors.move."+type);

            redirectAttributes.addFlashAttribute("errorContainer", error);
        }
        return "redirect:/"+type+"/view/"+id;
    }

    /**
     * Remove draft from revisionable. This is an actual removal and after the operation the data can not be found
     * from database anymore.
     * @param id - Id of the revisionable entity from where the draft is to be removed
     * @param type - Type of the removed object (used to return the user to correct page).
     * @return View name. If removal fails then the user is returned to the edit page (where the button is)
     *          otherwise they are returned to view the revisionable object
     */
    @RequestMapping(value="/remove/{type}/draft/{id}", method = RequestMethod.GET)
    public String removeDraft(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ErrorMessage error = new ErrorMessage();
        DraftRemoveResponse response = service.removeDraft(type, id);
        switch (response) {
            case SUCCESS:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.draft.complete");
                error.getData().add("general.errors.remove.draft.complete."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/view/"+id;
            case NO_DRAFT:
                error = new ErrorMessage();
                error.setTitle("general.errors.title.error");
                error.setMsg("general.errors.remove.draft.noDraft");
                error.getData().add("general.errors.remove.draft.noDraft."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/search";
            case NO_REVISIONABLE:
                error = new ErrorMessage();
                error.setTitle("general.errors.title.error");
                error.setMsg("general.errors.remove.draft.noObject");
                error.getData().add("general.errors.remove.draft.noObject."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/search";
            case FINAL_REVISION:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.draft.final");
                error.getData().add("general.errors.remove.draft.final."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/search";
        }
        return "redirect:/"+type+"/search";
    }

    /**
     * Remove draft from revisionable. This is an actual removal and after the operation the data can not be found
     * from database anymore.
     * @param id - Id of the revisionable entity from where the draft is to be removed
     * @param type - Type of the removed object (used to return the user to correct page).
     * @return View name. If removal fails then the user is returned to the edit page (where the button is)
     *          otherwise they are returned to view the revisionable object
     */
    @RequestMapping(value="/remove/{type}/logical/{id}", method = RequestMethod.GET)
    public String removeLogical(@PathVariable String type, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ErrorMessage error = new ErrorMessage();
        LogicalRemoveResponse response = service.removeLogical(type, id);
        switch (response) {
            case SUCCESS:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.logical.complete");
                error.getData().add("general.errors.remove.logical.complete."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/search";
            case OPEN_DRAFT:
                error = new ErrorMessage();
                error.setTitle("general.errors.title.error");
                error.setMsg("general.errors.remove.logical.draft");
                error.getData().add("general.errors.remove.logical.draft."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/view/"+id;
            case NO_REVISIONABLE:
                error = new ErrorMessage();
                error.setTitle("general.errors.title.error");
                error.setMsg("general.errors.remove.logical.noObject");
                error.getData().add("general.errors.remove.logical.noObject."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/search";
            case NO_APPROVED:
                error = new ErrorMessage();
                error.setTitle("general.errors.title.notice");
                error.setMsg("general.errors.remove.logical.noApproved");
                error.getData().add("general.errors.remove.logical.noApprived."+type);
                error.getData().add(id+"");

                redirectAttributes.addFlashAttribute("errorContainer", error);
                return "redirect:/"+type+"/view/"+id;
        }
        return "redirect:/"+type+"/search";
    }
}
