package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.HistoryService;
import fi.uta.fsd.metka.mvc.services.requests.ChangeCompareRequest;
import fi.uta.fsd.metka.mvc.services.simple.history.ChangeCompareSO;
import fi.uta.fsd.metka.mvc.services.simple.history.RevisionSO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public @ResponseBody List<RevisionSO> getRevisions(@PathVariable Long id) {
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
    public @ResponseBody ChangeCompareSO getChanges(@RequestBody ChangeCompareRequest request) throws Exception {
        //ChangeCompareSO c = service.compareRevisions(request);
        throw new Exception("Comparison not implemented yet!");
        //return c;
    }
}
