package fi.uta.fsd.metka.mvc.controller;

import codebook25.CodeBookDocument;
import fi.uta.fsd.metka.ddi.builder.DDIBuilder;
import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.simple.ErrorMessage;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.storage.repository.enums.SerializationResults;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.study.DDIExportRequest;
import fi.uta.fsd.metka.transfer.study.DDIExportResponse;
import fi.uta.fsd.metka.transfer.study.DDIImportRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private DDIBuilder ddiBuilder;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String catchAll() {
        return "redirect:/web/expert";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String catchAllSlash() {
        return "redirect:/web/expert";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        /*request.getSession(false).invalidate();
        SecurityContextHolder.clearContext();

        return "redirect:https://"+request.getServerName()+"/Shibboleth.sso/Logout";*/
        return "redirect:/j_spring_security_logout";
    }

    // TODO: Move to revision controller and unify as one call
    @RequestMapping(value = "prev/{type}/{id}", method = RequestMethod.GET)
    public String prev(@PathVariable ConfigurationType type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Pair<ReturnResult, Long> pair = service.getAdjancedRevisionableId(id, type, false);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            List<ErrorMessage> errors = new ArrayList<>();
            ErrorMessage error = new ErrorMessage();
            error.setMsg("general.errors.move.previous");
            error.getData().add("general.errors.move." + type);
            errors.add(error);

            redirectAttributes.addFlashAttribute("displayableErrors", errors);
        }
        return "redirect:/web/revision/"+type+"/view/"+id;

    }

    // TODO: Move to revision controller and unify as one call
    @RequestMapping(value = "next/{type}/{id}", method = RequestMethod.GET)
    public String next(@PathVariable ConfigurationType type, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Pair<ReturnResult, Long> pair = service.getAdjancedRevisionableId(id, type, true);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            List<ErrorMessage> errors = new ArrayList<>();
            ErrorMessage error = new ErrorMessage();
            error.setMsg("general.errors.move.next");
            error.getData().add("general.errors.move."+type);
            errors.add(error);

            redirectAttributes.addFlashAttribute("displayableErrors", errors);
        }
        return "redirect:/web/revision/"+type+"/view/"+id;
    }

    // TODO: Move to revision controller
    @RequestMapping(value="download/{id}/{no}", method = RequestMethod.GET)
    public HttpEntity<byte[]> downloadRevision(@PathVariable Long id, @PathVariable Integer no) {
        Pair<ReturnResult, RevisionData> pair = service.getRevisionData(id, no);
        if(pair.getLeft() != ReturnResult.REVISION_FOUND) {
            // TODO: Return error to user
            return null;
        } else {
            RevisionData revision = pair.getRight();
            Pair<SerializationResults, String> string = json.serialize(revision);
            if(string.getLeft() != SerializationResults.SERIALIZATION_SUCCESS) {
                // TODO: Return error to user
                return null;
            }
            byte[] dataBytes = string.getRight().getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Content-Disposition",
                    "attachment; filename=" + revision.getConfiguration().getType()
                            + "_id_" + revision.getKey().getId() + "_revision_" + revision.getKey().getNo() + ".json");
            headers.setContentLength(dataBytes.length);

            return new HttpEntity<>(dataBytes, headers);
        }
    }

    // TODO: Move to study controller
    @RequestMapping(value="revision/ddi/export", method = RequestMethod.POST)
    public @ResponseBody DDIExportResponse ddiExport(@RequestBody DDIExportRequest request) {
        Pair<ReturnResult, CodeBookDocument> pair = service.exportDDI(request.getId(), request.getNo(), request.getLanguage());
        DDIExportResponse response = new DDIExportResponse();
        if(pair.getLeft() != ReturnResult.OPERATION_SUCCESSFUL) {
            // Operation was not successful
            response.setResult(pair.getLeft());
            return response;
        }
        response.setResult(pair.getLeft());
        response.setContent(pair.getRight().toString());
        response.setId(request.getId());
        response.setNo(request.getNo());
        response.setLanguage(request.getLanguage() == Language.DEFAULT ? "fi" : request.getLanguage().toValue());
        return response;
    }

    // TODO: Move to study controller
    @RequestMapping(value="revision/ddi/import", method = RequestMethod.POST)
    public @ResponseBody ReturnResult ddiImport(@RequestBody DDIImportRequest request) {
        ReturnResult result = service.importDDI(request.getTransferData(), request.getPath());

        return result;
    }
}
