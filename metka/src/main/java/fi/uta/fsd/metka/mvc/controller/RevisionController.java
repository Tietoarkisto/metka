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

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.RevisionService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.expert.ExpertSearchQueryResponse;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metka.transfer.revisionable.RevisionableLogicallyRemovedRequest;
import fi.uta.fsd.metka.transfer.revisionable.RevisionableLogicallyRemovedResponse;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("revision")
public class RevisionController {
    @Autowired
    private RevisionService revisions;

    @RequestMapping(value = "adjacent", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse adjacentRevision(@RequestBody AdjacentRevisionRequest request) {
        return revisions.adjacentRevision(request);
    }

    @RequestMapping(value="ajax/approve", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse approve(@RequestBody TransferData transferData) {
        return revisions.approve(transferData);
    }

    @RequestMapping(value = "ajax/beginEdit", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse beginEdit(@RequestBody RevisionKey key) {
        return revisions.beginEditingRevision(key);
    }

    @RequestMapping(value = "ajax/claim", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse claim(@RequestBody RevisionKey key) {
        return revisions.claimRevision(key);
    }

    @RequestMapping(value = "ajax/configuration/{type}", method = RequestMethod.GET)
    public @ResponseBody ConfigurationResponse getConfiguration(@PathVariable ConfigurationType type) {
        return revisions.getConfiguration(type);
    }

    @RequestMapping(value="ajax/create", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse create(@RequestBody RevisionCreateRequest request) {
        RevisionDataResponse response = revisions.create(request);
        if(response.getResult().getResult().equals(ReturnResult.REVISION_CREATED.name())) {
            RevisionDataResponse claimResponse = revisions.claimRevision(response.getData().getKey());
            if(claimResponse.getResult().getResult().equals(ReturnResult.REVISION_UPDATE_SUCCESSFUL.name())) {
                claimResponse.setResult(response.getResult());
                return claimResponse;
            } else {
                return response;
            }
        } else return response;
    }

    @RequestMapping(value="ajax/edit", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse edit(@RequestBody TransferData transferData) {
        return revisions.edit(transferData.getKey());
    }

    @RequestMapping(value = "ajax/release", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse release(@RequestBody RevisionKey key) {
        return revisions.releaseRevision(key);
    }

    @RequestMapping(value="ajax/remove", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse remove(@RequestBody RevisionKey key, Boolean draft) {
        return revisions.remove(key, draft);
    }

    @RequestMapping(value="ajax/restore", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse restore(@RequestBody RevisionKey key) {
        return revisions.restore(key);
    }

    @RequestMapping(value="ajax/revert", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse revert(@RequestBody RevisionRevertRequest revertRequest){
        RevisionDataResponse response = revisions.revert(revertRequest.getKey(), revertRequest.getTargetNo());
        return response;
    }

    @RequestMapping(value="ajax/save", method = RequestMethod.POST)
    public @ResponseBody RevisionDataResponse save(@RequestBody TransferData transferData) {
        RevisionDataResponse response = revisions.save(transferData);
        return response;
    }

    @RequestMapping(value="ajax/search", method = RequestMethod.POST)
    public @ResponseBody
    ExpertSearchQueryResponse search(@RequestBody RevisionSearchRequest searchRequest) throws Exception {
        return revisions.search(searchRequest);
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
    public @ResponseBody RevisionDataResponse ajaxViewLatestRevisionWithType(@PathVariable ConfigurationType type, @PathVariable Long id) {
        return revisions.view(id);
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
    public @ResponseBody RevisionDataResponse ajaxViewRevisionWithType(@PathVariable ConfigurationType type, @PathVariable Long id, @PathVariable Integer no) {
        return revisions.view(id, no);
    }

    @RequestMapping(value="download", method = RequestMethod.POST)
    public @ResponseBody RevisionExportResponse downloadRevision(@RequestBody RevisionKey key) {
        return revisions.exportRevision(key);
    }

    @RequestMapping(value = "revisionCompare", method = RequestMethod.POST)
    public @ResponseBody RevisionCompareResponse revisionCompare(@RequestBody RevisionCompareRequest request) {
        return revisions.revisionCompare(request);
    }

    @RequestMapping(value = "revisionHistory", method = RequestMethod.POST)
    public @ResponseBody RevisionSearchResponse revisionHistory(@RequestBody RevisionHistoryRequest request) {
        return revisions.collectRevisionHistory(request);
    }

    @RequestMapping(value="search/{type}", method = RequestMethod.GET)
    public String search(@PathVariable ConfigurationType type, Model model) {

        ModelUtil.initRevisionModel(model, type);

        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value = "view/{type}/{id}", method = RequestMethod.GET)
    public String viewLatestRevision(@PathVariable ConfigurationType type, @PathVariable Long id, Model model) {
        // Take away types that shouldn't navigate through here
        switch(type) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, type, id);

        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value = "view/{type}/{id}/{no}", method = RequestMethod.GET)
    public String viewRevision(@PathVariable ConfigurationType type, @PathVariable Long id, @PathVariable Integer no, Model model) {
        // Take away types that shouldn't navigate through here
        switch(type) {
            case STUDY_VARIABLE:
            case STUDY_ATTACHMENT:
                // TODO: Return error
                return null;
        }

        ModelUtil.initRevisionModel(model, type, id, no);

        return AuthenticationUtil.getModelName("page", model);
    }

    /*
    * Gets a list of revisionable ids or revision keys,
    * and returns a list of pairs consisting of revision id/key => logically removed (boolean)
    * */
    @RequestMapping(value = "revisionablesLogicallyRemoved", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RevisionableLogicallyRemovedResponse revisionablesLogicallyRemoved(@RequestBody RevisionableLogicallyRemovedRequest request) {
        return revisions.revisionablesLogicallyRemoved(request);
    }
}