package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.enums.repositoryResponses.DraftRemoveResponse;
import fi.uta.fsd.metka.enums.repositoryResponses.LogicalRemoveResponse;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.simple.ErrorMessage;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Includes controllers for general functionality that doesn't warrant its own controller or doesn't fit anywhere else
 */
@Controller
public class GeneralController {

    @Autowired
    private GeneralService service;

    @Autowired
    private JSONUtil json;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/series/search";
    }

    @RequestMapping(value = "/prev/{type}/{id}", method = RequestMethod.GET)
    public String prev(@PathVariable String type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, false);
        } catch(NotFoundException e) {
            List<ErrorMessage> errors = new ArrayList<>();
            ErrorMessage error = new ErrorMessage();
            error.setMsg("general.errors.move.previous");
            error.getData().add("general.errors.move." + type);
            errors.add(error);

            redirectAttributes.addFlashAttribute("displayableErrors", errors);
        }
        return "redirect:/"+type+"/view/"+id;

    }

    @RequestMapping(value = "/next/{type}/{id}", method = RequestMethod.GET)
    public String next(@PathVariable String type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            id = service.getAdjancedRevisionableId(id, type, true);
        } catch(NotFoundException e) {
            List<ErrorMessage> errors = new ArrayList<>();
            ErrorMessage error = new ErrorMessage();
            error.setMsg("general.errors.move.next");
            error.getData().add("general.errors.move."+type);
            errors.add(error);

            redirectAttributes.addFlashAttribute("displayableErrors", errors);
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
    public String removeDraft(@PathVariable String type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        ErrorMessage error = new ErrorMessage();
        DraftRemoveResponse response = service.removeDraft(type, id);
        List<ErrorMessage> errors = new ArrayList<>();
        redirectAttributes.addFlashAttribute("displayableErrors", errors);
        switch (response.getResponse()) {
            case SUCCESS:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.draft.complete");
                error.getData().add("general.errors.remove.draft.complete."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/view/"+id;
            case NO_DRAFT:
                error = new ErrorMessage();
                error.setTitle("alert.error.title");
                error.setMsg("general.errors.remove.draft.noDraft");
                error.getData().add("general.errors.remove.draft.noDraft."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/search";
            case NO_REVISIONABLE:
                error = new ErrorMessage();
                error.setTitle("alert.error.title");
                error.setMsg("general.errors.remove.draft.noObject");
                error.getData().add("general.errors.remove.draft.noObject."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/search";
            case FINAL_REVISION:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.draft.final");
                error.getData().add("general.errors.remove.draft.final."+type);
                error.getData().add(id+"");
                errors.add(error);

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
    public String removeLogical(@PathVariable String type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        ErrorMessage error = new ErrorMessage();
        LogicalRemoveResponse response = service.removeLogical(type, id);
        List<ErrorMessage> errors = new ArrayList<>();
        redirectAttributes.addFlashAttribute("displayableErrors", errors);
        switch (response) {
            case SUCCESS:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.logical.complete");
                error.getData().add("general.errors.remove.logical.complete."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/search";
            case OPEN_DRAFT:
                error = new ErrorMessage();
                error.setTitle("alert.error.title");
                error.setMsg("general.errors.remove.logical.draft");
                error.getData().add("general.errors.remove.logical.draft."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/view/"+id;
            case NO_REVISIONABLE:
                error = new ErrorMessage();
                error.setTitle("alert.error.title");
                error.setMsg("general.errors.remove.logical.noObject");
                error.getData().add("general.errors.remove.logical.noObject."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/search";
            case NO_APPROVED:
                error = new ErrorMessage();
                error.setMsg("general.errors.remove.logical.noApproved");
                error.getData().add("general.errors.remove.logical.noApprived."+type);
                error.getData().add(id+"");
                errors.add(error);

                return "redirect:/"+type+"/view/"+id;
        }
        return "redirect:/"+type+"/search";
    }

    @RequestMapping(value="/download/{id}/{revision}", method = RequestMethod.GET)
    public HttpEntity<byte[]> downloadRevision(@PathVariable Long id, @PathVariable Integer revision) {
        String data = service.getRevisionData(id, revision);
        if(StringUtils.isEmpty(data)) {
            return null;
        }
        RevisionData revData = json.deserializeRevisionData(data);
        byte[] dataBytes = data.getBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Disposition",
                "attachment; filename=" + revData.getConfiguration().getType()
                        + "_id_" + revData.getKey().getId() + "_revision_" + revData.getKey().getRevision() + ".json");
        headers.setContentLength(dataBytes.length);

        return new HttpEntity<byte[]>(dataBytes, headers);
    }
}
