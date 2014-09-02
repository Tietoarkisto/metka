package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.SeriesService;
import fi.uta.fsd.metka.transfer.revision.RevisionSearchResponse;
import fi.uta.fsd.metka.transfer.series.SeriesAbbreviationsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles all requests for series operations such as view and save.
 * All requests contain base address /series
 */
@Controller
@RequestMapping("series")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @RequestMapping(value="getAbbreviations", method = RequestMethod.GET)
    public @ResponseBody SeriesAbbreviationsResponse getAbbreviations() {
        return seriesService.findAbbreviations();
    }

    @RequestMapping(value="getNames", method = RequestMethod.GET)
    public @ResponseBody RevisionSearchResponse getNames() {
        return seriesService.findNames();
    }
}
