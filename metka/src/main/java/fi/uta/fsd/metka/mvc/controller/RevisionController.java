package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.transfer.revision.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("revision")
public class RevisionController {
    @Autowired
    private RevisionService revisions;

    @RequestMapping("search/{type}")
    public String search(@PathVariable String type, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_VARIABLES:
                // TODO: Return error
                return null;
        }

        model.addAttribute("configurationType", ct);

        return "search";
    }

    @RequestMapping("view/{type}/{id}")
    public String viewLatestRevision(@PathVariable String type, @PathVariable Long id, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_VARIABLES:
                // TODO: Return error
                return null;
        }

        model.addAttribute("configurationType", ct);
        model.addAttribute("revisionId", id);

        return "view";
    }

    @RequestMapping("view/{type}/{id}/{revision}")
    public String viewRevision(@PathVariable String type, @PathVariable Long id, @PathVariable Integer no, Model model) {
        if(!ConfigurationType.isValue(type.toUpperCase())) {
            // TODO: Return error
            return null;
        }

        ConfigurationType ct = ConfigurationType.fromValue(type.toUpperCase());
        // Take away types that shouldn't navigate through here
        switch(ct) {
            case STUDY_VARIABLE:
            case STUDY_VARIABLES:
                // TODO: Return error
                return null;
        }

        model.addAttribute("configurationType", ct);
        model.addAttribute("revisionId", id);
        model.addAttribute("revisionNo", no);

        return "view";
    }

    /**
     * Returns latest revision data and related configurations.
     * This operation checks that data is of requested type.
     *
     * @param id RevisionableId of the requested revision
     * @param type ConfigurationType that the requested revision should be
     * @return RevisionDataResponse object containing the revision data as TransferData, Configuration with specific version and the newest GUIConfiguration for the revision type
     */
    @RequestMapping(value = "ajax/view/{type}/{id}", method = RequestMethod.GET)
    public @ResponseBody RevisionDataResponse ajaxViewLatestRevisionWithType(@PathVariable String type, @PathVariable Long id) {
        return revisions.view(id, type.toUpperCase());
    }

    /**
     * Returns a revision data and related configurations.
     * This operation checks that data is of requested type.
     *
     * @param id RevisionableId of the requested revision
     * @param no Revision number of the requested revision
     * @param type ConfigurationType that the requested revision should be
     * @return RevisionDataResponse object containing the revision data as TransferData, Configuration with specific version and the newest GUIConfiguration for the revision type
     */
    @RequestMapping(value = "ajax/view/{type}/{id}/{no}", method = RequestMethod.GET)
    public @ResponseBody RevisionDataResponse ajaxViewRevisionWithType(@PathVariable String type, @PathVariable Long id, @PathVariable Integer no) {
        return revisions.view(id, no, type.toUpperCase());
    }

    @RequestMapping(value="ajax/create", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse create(@RequestBody RevisionCreateRequest request) {
        return revisions.create(request);
    }

    @RequestMapping(value="ajax/edit", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse edit(@RequestBody TransferData transferData) {
        return revisions.edit(transferData);
    }

    @RequestMapping(value="ajax/save", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse save(@RequestBody TransferData transferData) {
        return revisions.save(transferData);
    }

    @RequestMapping(value="ajax/approve", method = RequestMethod.POST)
    public @ResponseBody RevisionOperationResponse approve(@RequestBody TransferData transferData) {
        return revisions.approve(transferData);
    }

    @RequestMapping(value="ajax/search/{type}", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody RevisionSearchResponse search(@RequestBody RevisionSearchRequest searchRequest, @PathVariable String type) {
        return revisions.search(searchRequest, type);
    }

    /**
     * Search for series. Returns search data, wrapped in a map, as json.
     *
     * @param searchData search data
     * @return search data wrapped in a map as json
     */
    /*@RequestMapping(value="ajax/search"
            , method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody Map<String, Object> ajaxSearch(
            @RequestBody SeriesSearchData searchData) {
        Map<String, Object> map = new HashMap<>();
        List<ErrorMessage> errors = new ArrayList<>();

        if(searchData.getQuery() != null) {
            List<SearchResult> results = seriesService
                    .searchForSeries(searchData.getQuery());
            searchData.setResults(results);
            searchData.setQuery(searchData.getQuery());
        }

        searchData.setAbbreviations(seriesService.findAbbreviations());

        if(searchData.getQuery() != null
                && searchData.getResults().size() == 0) {
            errors.add(ErrorMessage.noResults("series"));
        }

        map.put("searchData", searchData);
        map.put("displayableErrors", errors);

        return map;
    }*/

    /**
     * Save series. Returns status and messages of the operation, wrapped in a
     * map, as json.
     *
     * TODO: Generalize to revision/ajax/save
     *
     * @param transferData transfer object as json
     * @param response http servlet response
     * @return status and messages of operation in a map as json
     */
    /*@RequestMapping(value="ajax/save"
            , method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> ajaxSave(
            @RequestBody TransferData transferData
            , HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        List<ErrorMessage> errors = new ArrayList<>();

        boolean success = seriesService.saveSeries(transferData);

        if(success) {
            errors.add(ErrorMessage.saveSuccess());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errors.add(ErrorMessage.saveFail());
        }

        map.put("success", success);
        map.put("errors", errors);

        return map;
    }*/

    /**
     * Approve series. Returns status and messages of the operation, wrapped
     * in a map, as json.
     *
     * TODO: Generalize to revision/ajax/approve (remember that STUDY_ATTACHMENT STUDY_VARIABLES and STUDY_VARIABLE are not approved by user but by approving the study)
     *
     * @param transferData transfer object as json
     * @param response http servlet response
     * @return status and messages of operation in a map as json
     */
    /*@RequestMapping(value="ajax/approve"
            , method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> ajaxApprove(
            @RequestBody TransferData transferData
            , HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        List<ErrorMessage> errors = new ArrayList<>();

        boolean success = seriesService.saveSeries(transferData);

        if(!success) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            errors.add(ErrorMessage.approveFailSave());
        } else {
            success = seriesService.approveSeries(transferData);

            if(!success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                errors.add(ErrorMessage.approveFailValidate());
            } else {
                errors.add(ErrorMessage.approveSuccess());
            }
        }

        map.put("success", success);
        map.put("errors", errors);

        return map;
    }*/
}
