package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ConfigurationService;
import fi.uta.fsd.metka.mvc.services.GeneralService;
import fi.uta.fsd.metka.mvc.services.SeriesService;
import fi.uta.fsd.metka.storage.util.JSONUtil;
import fi.uta.fsd.metka.transfer.revision.SeriesAbbreviationsResponse;
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
@RequestMapping("/series")
public class SeriesController {

    @Autowired
    private SeriesService seriesService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private JSONUtil json;
    @Autowired
    private GeneralService general;

    @RequestMapping(value="getAbbreviations", method = RequestMethod.GET)
    public @ResponseBody
    SeriesAbbreviationsResponse getAbbreviations() {
        return seriesService.findAbbreviations();
    }



    /*
    * Edit series
    * Requests an editable revision for the series. Everything required to get an editable
    * revision for the user is done further down the line (e.g. checking if new revision is
    * actually required or is there already an open DRAFT revision).
    * TODO: Change to ajax request
    */
    /*@RequestMapping(value = "edit/{seriesno}", method = {RequestMethod.GET})
    public String edit(@PathVariable Long seriesno, RedirectAttributes redirectAttributes) {
        RevisionViewDataContainer revData = seriesService.editSeries(seriesno);
        if(revData == null || revData.getTransferObject() == null || revData.getConfiguration() == null) {
            // TODO: Notify user that no editable revision could be found or created
            return REDIRECT_VIEW+seriesno;
        } else {
            redirectAttributes.addFlashAttribute("single", revData.getTransferObject());
            redirectAttributes.addFlashAttribute("seriesconfiguration", revData.getConfiguration());
            return REDIRECT_VIEW+revData.getTransferObject().getId()+"/"+revData.getTransferObject().getRevision();
        }
    }*/



    /*
    * Approve series
    * First makes sure that series is saved and if successful then requests series approval.
    * Since only DRAFTs can be approved and only the latest revision can be a DRAFT
    * only the series id is needed for the approval process. All required validation is done
    * later in the approval process.
    */
    /*@RequestMapping(value="approve", method = {RequestMethod.POST})
    public String approve(@ModelAttribute("single")TransferObject single, RedirectAttributes redirectAttributes) {
        boolean success = seriesService.saveSeries(single);
        List<ErrorMessage> errors = new ArrayList<>();
        if(!success) {
            errors.add(ErrorMessage.approveFailSave());
        } else {
            success = seriesService.approveSeries(single);

            if(!success) {
                errors.add(ErrorMessage.approveFailValidate());
            } else {
                errors.add(ErrorMessage.approveSuccess());
            }
        }

        if(errors.size() > 0) redirectAttributes.addFlashAttribute("displayableErrors", errors);
        return REDIRECT_VIEW+single.getId()+"/"+single.getRevision();
    }*/
}
