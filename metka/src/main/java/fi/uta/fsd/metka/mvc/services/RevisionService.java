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

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.general.RevisionKey;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.transfer.revision.*;
import fi.uta.fsd.metkaAuthentication.Permission;
import fi.uta.fsd.metkaAuthentication.PermissionCheck;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@PreAuthorize("hasPermission('"+ Permission.Values.CAN_VIEW_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
@Transactional(noRollbackFor = {NumberFormatException.class})
public interface RevisionService {

    @Transactional(readOnly = true) RevisionDataResponse view(Long id);

    @Transactional(readOnly = true) RevisionDataResponse view(Long id, Integer no);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_CREATE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionDataResponse create(RevisionCreateRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionDataResponse edit(RevisionKey key);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionDataResponse save(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') ")
    RevisionDataResponse createAndSave(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') ")
    MassRevisionDataResponse massCreateFiles(List<TransferData> transferDatas);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_APPROVE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#transferData, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionDataResponse approve(TransferData transferData);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_REMOVE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.IS_HANDLER + "')")
    RevisionDataResponse remove(RevisionKey key, Boolean draft);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_RESTORE_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionDataResponse restore(RevisionKey key);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "')")
    RevisionDataResponse revert(RevisionKey key, Integer targetRevision);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_PERFORM_SEARCH +"', '" + PermissionCheck.Values.PERMISSION + "')")
    @Transactional(readOnly = true) RevisionSearchResponse search(RevisionSearchRequest request);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.CLAIM_REVISION + "')")
    RevisionDataResponse beginEditingRevision(RevisionKey key);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.CLAIM_REVISION + "')")
    RevisionDataResponse claimRevision(RevisionKey key);

    @PreAuthorize("hasPermission('"+ Permission.Values.CAN_EDIT_REVISION +"', '" + PermissionCheck.Values.PERMISSION + "') " +
            "and hasPermission(#key, '" + PermissionCheck.Values.RELEASE_REVISION + "')")
    RevisionDataResponse releaseRevision(RevisionKey key);

    @Transactional(readOnly = true) RevisionSearchResponse collectRevisionHistory(RevisionHistoryRequest request);

    @Transactional(readOnly = true) RevisionCompareResponse revisionCompare(RevisionCompareRequest request);

    @Transactional(readOnly = true) ConfigurationResponse getConfiguration(ConfigurationType type);
    @Transactional(readOnly = true) ConfigurationResponse getConfiguration(ConfigurationKey key);

    @Transactional(readOnly = true) RevisionDataResponse adjacentRevision(AdjacentRevisionRequest request);

    @Transactional(readOnly = true) RevisionExportResponse exportRevision(RevisionKey key);
}
