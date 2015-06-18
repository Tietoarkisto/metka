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

package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.ButtonType;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.util.HashSet;
import java.util.Set;
/**
 * Specification and documentation is found from uml/gui_config/uml_json_gui_configuration_button.graphml
 */
@JsonIgnoreProperties("_comment")
public class Button {
    private TranslationObject title;
    private final Set<String> permissions = new HashSet<>();
    private String isHandledByUser;
    private Boolean isHandler;
    private Boolean hasHandler;
    private final Set<VisibilityState> states = new HashSet<>();
    private ButtonType type;
    private String customHandler;

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public String getIsHandledByUser() {
        return isHandledByUser;
    }

    public void setIsHandledByUser(String isHandledByUser) {
        this.isHandledByUser = isHandledByUser;
    }

    public Boolean getIsHandler() {
        return isHandler;
    }

    public void setIsHandler(Boolean isHandler) {
        this.isHandler = isHandler;
    }

    public Boolean getHasHandler() {
        return hasHandler;
    }

    public void setHasHandler(Boolean hasHandler) {
        this.hasHandler = hasHandler;
    }

    public Set<VisibilityState> getStates() {
        return states;
    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public String getCustomHandler() {
        return customHandler;
    }

    public void setCustomHandler(String customHandler) {
        this.customHandler = customHandler;
    }
}
