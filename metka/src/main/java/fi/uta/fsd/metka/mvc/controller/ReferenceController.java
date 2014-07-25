package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.transfer.reference.*;
import fi.uta.fsd.metka.mvc.services.simple.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Handles all requests pertaining to references such as collecting reference options.
 */
@Controller
@RequestMapping("/references")
public class ReferenceController {
    @Autowired
    private ReferenceService service;

    @RequestMapping(value = "collectOptionsGroup", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ReferenceOptionsGroupResponse collectReferenceOptionsGroup(@RequestBody ReferenceOptionsGroupRequest requests) {
        ReferenceOptionsGroupResponse responses = new ReferenceOptionsGroupResponse(requests.getKey());
        for(ReferenceOptionsRequest request : requests.getRequests()) {
            ReferenceOptionsResponse response = new ReferenceOptionsResponse(request.getKey(), request.getContainer());
            response.setDependencyValue(request.getDependencyValue());

            List<ReferenceOption> options = null;
            try {
                options = service.collectReferenceOptions(request);
            } catch(IOException ex) {
                ex.printStackTrace();
                ErrorMessage message = new ErrorMessage();
                message.setMsg("general.errors.reference.exceptionWhileCollecting");
                response.getMessages().add(message);
            }
            if(options == null) {
                ErrorMessage message = new ErrorMessage();
                message.setMsg("general.errors.reference.exceptionBeforeCollecting");
                response.getMessages().add(message);
            } else {
                response.setOptions(options);
            }
            responses.getResponses().add(response);
        }

        return responses;
    }
}
