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

package fi.uta.fsd.metkaAuthentication;

public  enum Permission {
    // Valid role
    HAS_MINIMUM_PERMISSION(Values.HAS_MINIMUM_PERMISSION),

    // Search permissions
    CAN_PERFORM_SEARCH(Values.CAN_PERFORM_SEARCH),
    CAN_SAVE_EXPERT_SEARCH(Values.CAN_SAVE_EXPERT_SEARCH),
    CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH(Values.CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH),

    // General revision permissions
    CAN_VIEW_REVISION(Values.CAN_VIEW_REVISION),
    CAN_CREATE_REVISION(Values.CAN_CREATE_REVISION),
    CAN_EDIT_REVISION(Values.CAN_EDIT_REVISION),
    CAN_APPROVE_REVISION(Values.CAN_APPROVE_REVISION),
    CAN_FORCE_CLAIM_REVISION(Values.CAN_FORCE_CLAIM_REVISION),
    CAN_FORCE_RELEASE_REVISION(Values.CAN_FORCE_RELEASE_REVISION),
    CAN_REMOVE_REVISION(Values.CAN_REMOVE_REVISION),
    CAN_RESTORE_REVISION(Values.CAN_RESTORE_REVISION),
    CAN_REVERT_REVISION(Values.CAN_REVERT_REVISION),

    // Binder
    CAN_VIEW_BINDER_PAGES(Values.CAN_VIEW_BINDER_PAGES),
    CAN_EDIT_BINDER_PAGES(Values.CAN_EDIT_BINDER_PAGES),

    // Import and Export permissions
    CAN_IMPORT_REVISION(Values.CAN_IMPORT_REVISION),
    CAN_EXPORT_REVISION(Values.CAN_EXPORT_REVISION),

    // Study specific permissions
    CAN_REMOVE_STUDY_VERSIONS(Values.CAN_REMOVE_STUDY_VERSIONS),
    CAN_VIEW_STUDY_ERRORS(Values.CAN_VIEW_STUDY_ERRORS),
    CAN_EDIT_STUDY_ERRORS(Values.CAN_EDIT_STUDY_ERRORS),
    CAN_REMOVE_STUDY_ERRORS(Values.CAN_REMOVE_STUDY_ERRORS),
    CAN_ADD_ORGANIZATIONS(Values.CAN_ADD_ORGANIZATIONS),

    // Program settings permissions
    CAN_VIEW_SETTINGS_PAGE(Values.CAN_VIEW_SETTINGS_PAGE),
    CAN_GENERATE_REPORTS(Values.CAN_GENERATE_REPORTS),
    CAN_UPLOAD_CONFIGURATIONS(Values.CAN_UPLOAD_CONFIGURATIONS),
    CAN_UPLOAD_JSON(Values.CAN_UPLOAD_JSON),
    CAN_VIEW_INDEX_INFO(Values.CAN_VIEW_INDEX_INFO),
    CAN_MANUALLY_INDEX_CONTENT(Values.CAN_MANUALLY_INDEX_CONTENT),
    CAN_VIEW_API_USERS(Values.CAN_VIEW_API_USERS),
    CAN_EDIT_API_USERS(Values.CAN_EDIT_API_USERS);

    private final String permission;

    private Permission(String permission) {
        this.permission = permission;
    }

    public String toPermission() {
        return permission;
    }

    public static boolean isPermission(String permission) {
        for(Permission p : values()) {
            if(p.permission.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public static Permission fromPermission(String permission) {
        for(Permission p : values()) {
            if(p.permission.equals(permission)) {
                return p;
            }
        }
        throw new RuntimeException("Not a valid permission name");
    }

    public static final class Values {
        // Valid role
        public static final String HAS_MINIMUM_PERMISSION = "hasMinimumPermission";

        // Search permissions
        public static final String CAN_PERFORM_SEARCH = "canPerformSearch";
        public static final String CAN_SAVE_EXPERT_SEARCH = "canSaveExpertSearch";
        public static final String CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH = "canRemoveNotOwnedExpertSearch";

        // General revision permissions
        public static final String CAN_VIEW_REVISION = "canViewRevision";
        public static final String CAN_CREATE_REVISION = "canCreateRevision";
        public static final String CAN_EDIT_REVISION = "canEditRevision";
        public static final String CAN_APPROVE_REVISION = "canApproveRevision";
        public static final String CAN_FORCE_CLAIM_REVISION = "canForceClaimRevision";
        public static final String CAN_FORCE_RELEASE_REVISION = "canForceReleaseRevision";
        public static final String CAN_REMOVE_REVISION = "canRemoveRevision";
        public static final String CAN_RESTORE_REVISION = "canRestoreRevision";
        public static final String CAN_REVERT_REVISION = "canRevertRevision";

        // Binder
        public static final String CAN_VIEW_BINDER_PAGES = "canViewBinderPages";
        public static final String CAN_EDIT_BINDER_PAGES = "canEditBinderPages";

        // Import and Export permissions
        public static final String CAN_IMPORT_REVISION = "canImportRevision";
        public static final String CAN_EXPORT_REVISION = "canExportRevision";

        // Study specific permissions
        public static final String CAN_REMOVE_STUDY_VERSIONS = "canRemoveStudyVersions";
        public static final String CAN_VIEW_STUDY_ERRORS = "canViewStudyErrors";
        public static final String CAN_EDIT_STUDY_ERRORS = "canAddStudyErrors";
        public static final String CAN_REMOVE_STUDY_ERRORS = "canRemoveStudyErrors";
        public static final String CAN_ADD_ORGANIZATIONS = "canAddOrganizations";

        // Program settings permissions
        public static final String CAN_VIEW_SETTINGS_PAGE = "canViewSettingsPage";
        public static final String CAN_GENERATE_REPORTS = "canGenerateReports";
        public static final String CAN_UPLOAD_CONFIGURATIONS = "canUploadConfigurations";
        public static final String CAN_UPLOAD_JSON = "canUploadJson";
        public static final String CAN_VIEW_INDEX_INFO = "canViewIndexInfo";
        public static final String CAN_MANUALLY_INDEX_CONTENT = "canManuallyIndexContent";
        public static final String CAN_VIEW_API_USERS = "canViewAPIUsers";
        public static final String CAN_EDIT_API_USERS = "canEditAPIUsers";
    }
}