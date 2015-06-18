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

package fi.uta.fsd.metka.mvc;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metkaAuthentication.AuthenticationUtil;
import fi.uta.fsd.metkaAuthentication.MetkaAuthenticationDetails;
import org.springframework.ui.Model;

/**
 * Provides methods for handling mvc model and groups different functionality together
 */
public final class ModelUtil {
    private ModelUtil() {}

    private static void addUserInfo(Model model) {
        MetkaAuthenticationDetails details = AuthenticationUtil.getAuthenticationDetails();
        model.asMap().put("uDisplayName", details.getDisplayName());
        model.asMap().put("uUserName", details.getUserName());
        model.asMap().put("uRole", details.getRole().toJsonString());
        model.asMap().put("uDefLang", details.getRole().getDefaultLanguage().toValue());
    }

    public static void initRevisionModel(Model model, ConfigurationType type) {
        initRevisionModel(model, type, null);
    }

    public static void initRevisionModel(Model model, ConfigurationType type, Long id) {
        initRevisionModel(model, type, id, null);
    }

    public static void initRevisionModel(Model model, ConfigurationType type, Long id, Integer no) {
        revisionModel(model, type, id, no);
    }

    private static void revisionModel(Model model, ConfigurationType type, Long id, Integer no) {
        addUserInfo(model);
        model.asMap().put("configurationType", type);
        if(id != null) model.asMap().put("revisionId", id);
        if(no != null) model.asMap().put("revisionNo", no);
    }

    public static void initExpertSearch(Model model) {
        addUserInfo(model);
        model.asMap().put("configurationType", "EXPERT");
    }

    public static void initSettings(Model model) {
        addUserInfo(model);
        model.asMap().put("configurationType", "SETTINGS");
    }

    public static void initBinder(Model model) {
        addUserInfo(model);
        model.asMap().put("configurationType", "BINDER");
    }
}
