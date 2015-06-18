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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.DisplayType;
import fi.uta.fsd.metka.enums.Language;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification and documentation is found from uml/gui_config/uml_json_gui_configuration_FieldDescription.graphml
 */
@JsonIgnoreProperties("_comment")
public class FieldDescription {
    private final String key;
    private DisplayType displayType; // Can be ignored for now, only defined here for completeness
    private Boolean multiline;
    private Boolean multichoice; // Can be ignored for now, only defined here for completeness
    private final List<String> columnFields = new ArrayList<>();
    private Boolean showSaveInfo;
    private Boolean showReferenceValue;
    private Boolean displayHeader = true;
    private DialogTitle dialogTitle;
    private Boolean showReferenceSaveInfo = false;
    private final List<Language> showReferenceApproveInfo = new ArrayList<>();
    private Boolean showReferenceState = false;

    @JsonCreator
    public FieldDescription(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public Boolean getMultiline() {
        return (multiline == null) ? false : multiline;
    }

    public void setMultiline(Boolean multiline) {
        this.multiline = (multiline == null) ? false : multiline;
    }

    public Boolean getMultichoice() {
        return (multichoice == null) ? false : multichoice;
    }

    public void setMultichoice(Boolean multichoice) {
        this.multichoice = (multichoice == null) ? false : multichoice;
    }

    public List<String> getColumnFields() {
        return columnFields;
    }

    public Boolean getShowSaveInfo() {
        return (showSaveInfo == null) ? false : showSaveInfo;
    }

    public void setShowSaveInfo(Boolean showSaveInfo) {
        this.showSaveInfo = (showSaveInfo == null) ? false : showSaveInfo;
    }

    public Boolean getShowReferenceValue() {
        return (showReferenceValue == null) ? false : showReferenceValue;
    }

    public void setShowReferenceValue(Boolean showReferenceValue) {
        this.showReferenceValue = (showReferenceValue == null) ? false : showReferenceValue;
    }

    public Boolean getDisplayHeader() {
        return displayHeader == null ? true : displayHeader;
    }

    public void setDisplayHeader(Boolean displayHeader) {
        this.displayHeader = displayHeader == null ? true : displayHeader;
    }

    public DialogTitle getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(DialogTitle dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public Boolean getShowReferenceSaveInfo() {
        return showReferenceSaveInfo == null ? false : showReferenceSaveInfo;
    }

    public void setShowReferenceSaveInfo(Boolean showReferenceSaveInfo) {
        this.showReferenceSaveInfo = (showReferenceSaveInfo == null ? false : showReferenceSaveInfo);
    }

    public List<Language> getShowReferenceApproveInfo() {
        return showReferenceApproveInfo;
    }

    public Boolean getShowReferenceState() {
        return showReferenceState == null ? false : showReferenceState;
    }

    public void setShowReferenceState(Boolean showReferenceState) {
        this.showReferenceState = (showReferenceState == null ? false : showReferenceState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldDescription that = (FieldDescription) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}