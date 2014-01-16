package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.HistoryService;
import fi.uta.fsd.metka.mvc.domain.simple.history.RevisionSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/14/14
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/history")
public class HistoryController {
    @Autowired
    private HistoryService service;

    // Handle requests for revision history
    @RequestMapping(value = "revisions/{id}", method = {RequestMethod.GET, RequestMethod.POST},
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<RevisionSO> getRevisions(@PathVariable Integer id) {
        return service.getRevisionHistory(id);
    }

    // Show revision history for revisionable

    // Find changes comparing two revisions
    // 1. Get oldRevision+1
    // 2. Get changes
    // 3. Repeat combining changes always leaving the latest change for every field until at current revision
    // 4. Make a history object suitable for showing on page.

}
