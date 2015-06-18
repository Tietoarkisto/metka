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

import fi.uta.fsd.metka.mvc.ModelUtil;
import fi.uta.fsd.metka.mvc.services.BinderService;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.binder.BinderListResponse;
import fi.uta.fsd.metka.transfer.binder.SaveBinderPageRequest;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("binder")
public class BinderController {
    @Autowired
    private BinderService service;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String settings(Model model) {
        ModelUtil.initBinder(model);
        return AuthenticationUtil.getModelName("page", model);
    }

    @RequestMapping(value="binderContent/{binderId}", method = RequestMethod.GET)
    public @ResponseBody
    BinderListResponse binderContent(@PathVariable Long binderId) {
        return service.binderContent(binderId);
    }

    @RequestMapping(value="listBinderPages", method = {RequestMethod.GET})
    public @ResponseBody
    BinderListResponse listBinderPages() {
        return service.listBinderPages();
    }

    @RequestMapping(value="listStudyBinderPages/{id}", method = RequestMethod.GET)
    public @ResponseBody
    BinderListResponse listStudyBinderPages(@PathVariable Long id) {
        return service.listStudyBinderPages(id);
    }

    @RequestMapping(value = "removePage/{pageId}", method = RequestMethod.GET)
    public @ResponseBody ReturnResult removePage(@PathVariable Long pageId) {
        return service.removePage(pageId);
    }

    @RequestMapping(value="saveBinderPage", method = RequestMethod.POST)
    public @ResponseBody BinderListResponse saveBinderPage(@RequestBody SaveBinderPageRequest request) {
        return service.saveBinderPage(request);
    }
}
