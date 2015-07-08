/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.mvc.controller;

import fi.uta.fsd.metka.mvc.services.ReferenceService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.reference.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles all requests pertaining to references such as collecting reference options.
 */
@Controller
@RequestMapping("references")
public class ReferenceController {
    @Autowired
    private ReferenceService service;

    // TODO: Phase this out and move everything over to path based reference handling
    @RequestMapping(value = "collectOptionsGroup", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ReferenceOptionsGroupResponse collectReferenceOptionsGroup(@RequestBody ReferenceOptionsGroupRequest requests) {
        ReferenceOptionsGroupResponse responses = new ReferenceOptionsGroupResponse(requests.getKey());
        for(ReferenceOptionsRequest request : requests.getRequests()) {
            ReferenceOptionsResponse response = new ReferenceOptionsResponse(request.getKey(), request.getContainer(), request.getLanguage(), request.getFieldValues());

            List<ReferenceOption> options = service.collectReferenceOptions(request);

            response.setOptions(options);
            responses.getResponses().add(response);
        }

        return responses;
    }

    /**
     * Handles a group request of reference paths.
     * This is an advanced version of the normal request in that the user is required to provide all of the references.
     * This can be used to implement custom references on UI or requesting side and thus is much more powerful than the normal request of parsing
     * configuration defined references. Thus it might we warranted to check somewhat more clearly what user is doing but for now this functions in
     * good faith that users don't abuse this system.
     * @param requests
     * @return
     */
    @RequestMapping(value = "referencePathGroup", method = {RequestMethod.POST},
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ReferencePathGroupResponse collectReferencePathGroup(@RequestBody ReferencePathGroupRequest requests) {
        ReferencePathGroupResponse responses = new ReferencePathGroupResponse(requests.getKey());
        for(ReferencePathRequest request : requests.getRequests()) {
            ReferencePathResponse response = new ReferencePathResponse(request.getKey(), request.getContainer(), request.getLanguage(), request.getRoot());

            List<ReferenceOption> options = service.collectReferenceOptions(request);

            response.getOptions().addAll(options);
            responses.getResponses().add(response);
        }

        return responses;
    }

    // TODO: Refactor to RevisionController as "revisionableStatus"
    @RequestMapping(value = "referenceStatus/{id}", method = RequestMethod.GET)
    public @ResponseBody ReferenceStatusResponse referenceStatus(@PathVariable String id) {
        // TODO: if this can be split then we want status for a specific revision, otherwise use the current method
        // TODO: Also check if all given values are of their proper type and if not then return PARAMETERS_MISSING
        if(!StringUtils.hasText(id)) {
            return ReferenceStatusResponse.returnResultResponse(ReturnResult.PARAMETERS_MISSING);
        }
        String[] splits = id.split("-");
        if(splits == null || splits.length < 2) {
            // Use the old method
            try {
                return service.getReferenceStatus(Long.parseLong(id));
            } catch (NumberFormatException nfe) {
                return ReferenceStatusResponse.returnResultResponse(ReturnResult.PARAMETERS_MISSING);
            }
        } else {
            // Use method for specific revision
            try {
                return service.getReferenceStatus(Long.parseLong(splits[0]), Integer.parseInt(splits[1]));
            } catch (NumberFormatException nfe) {
                return ReferenceStatusResponse.returnResultResponse(ReturnResult.PARAMETERS_MISSING);
            }
        }
    }
}
