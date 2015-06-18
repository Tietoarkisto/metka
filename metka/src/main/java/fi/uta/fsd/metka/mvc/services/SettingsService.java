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

package fi.uta.fsd.metka.mvc.services;

import com.fasterxml.jackson.databind.JsonNode;
import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;
import fi.uta.fsd.metka.transfer.settings.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@PreAuthorize("hasPermission('canViewSettingsPage', 'PERMISSION')")
@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_SETTINGS_PAGE +"', 'PERMISSION')")
@Transactional
public interface SettingsService {
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    ReturnResult uploadConfiguration(Configuration configuration);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    ReturnResult uploadConfiguration(GUIConfiguration configuration);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    ReturnResult uploadJson(JsonNode misc);

    // Uses report repository to generate example report
    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_GENERATE_REPORTS +"', 'PERMISSION')")
    @Transactional(readOnly = true) String generateReport();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_API_USERS +"', 'PERMISSION')")
    @Transactional(readOnly = true) APIUserListResponse listAPIUsers();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_API_USERS +"', 'PERMISSION')")
    APIUserListResponse newAPIUser(NewAPIUserRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_API_USERS +"', 'PERMISSION')")
    ReturnResult removeAPIUser(String publicKey);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION') OR " +
            "hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    List<JSONListEntry> getJsonList(UploadJsonRequest.JsonType type);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION') OR " +
            "hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    String getJsonContent(JSONListEntry entry);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_INDEX_INFO +"', 'PERMISSION')")
    OpenIndexCommandsResponse getOpenIndexCommands();

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_MANUALLY_INDEX_CONTENT +"', 'PERMISSION')")
    ReturnResult indexEverything();

    /*@PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    void uploadDataConfig(MultipartFile file) throws IOException;

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_CONFIGURATIONS +"', 'PERMISSION')")
    void uploadGuiConfig(MultipartFile file) throws IOException;

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_UPLOAD_JSON +"', 'PERMISSION')")
    void uploadJson(MultipartFile file) throws IOException;*/
}
