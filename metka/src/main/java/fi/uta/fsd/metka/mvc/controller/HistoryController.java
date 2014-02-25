package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.domain.HistoryService;
import fi.uta.fsd.metka.mvc.domain.requests.ChangeCompareRequest;
import fi.uta.fsd.metka.mvc.domain.simple.history.ChangeCompareSO;
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

    /**
     * Get list of revisions for revisionable object.
     * @param id - Id of revisionable object for which revisions are requested
     * @return List of RevisionSO objects containing all existing revisions for given revisionable
     */
    @RequestMapping(value = "revisions/{id}", method = {RequestMethod.GET},
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<RevisionSO> getRevisions(@PathVariable Integer id) {
        return service.getRevisionHistory(id);
    }

    /**
     * Get comparison of an inclusive revision range given in the request.
     * Only latest changes to each field are included.
     * @param request - Request object containing revision range and other needed information.
     * @return ChangeComparison object containing all relevant data for the revision range comparison
     */
    @RequestMapping(value = "revisions/compare", method = {RequestMethod.GET, RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ChangeCompareSO getChanges(@RequestBody ChangeCompareRequest request) {
        ChangeCompareSO c = service.compareRevisions(request);
        return c;
    }
}
