package fi.uta.fsd.metkaAuthentication;

import fi.uta.fsd.metka.enums.Language;

/**
 * Normalized role for Metka-application. Implemented as a class instead of enum for ease of use with web page.
 * Role permissions are layered so that higher permissions can do anything lower permissions can and more.
 * These are just generalizations of the permissions each role has and are not conclusive descriptions.
 *
 * UNKNOWN role is reserved for authentications that provided valid credentials but have a role that is not recognized. They can't do anything.
 * READER has basic rights to view revision data and perform searches as well as generating reports
 * USER has rights to manage revisions by creating, editing, approving and removing revisions in various states. They can also claim revision being edited by someone else
 * TRANSLATOR has the same rights as USER but has english as their default language. This is a separate branch and DATA_ADMIN uses USER role as base.
 * DATA_ADMIN can remove rows from study versions (specialized containers in study) and can upload configurations and json files to the system through settings
 * ADMIN can do everything that the system can do through the web page. On server side they should also have access to the database and file server.
 */
public final class MetkaRole implements Comparable<MetkaRole> {
    public static final MetkaRole UNKNOWN = MetkaRoleBuilder.getUnknown().build();
    public static final MetkaRole READER = MetkaRoleBuilder.getReader().build();
    public static final MetkaRole USER = MetkaRoleBuilder.getUser().build();
    public static final MetkaRole TRANSLATOR = MetkaRoleBuilder.getTranslator().build();
    public static final MetkaRole DATA_ADMIN = MetkaRoleBuilder.getDataAdmin().build();
    public static final MetkaRole ADMIN = MetkaRoleBuilder.getAdmin().build();

    public static MetkaRole fromRoleName(MetkaRoleName roleName) {
        switch(roleName) {
            case UNKNOWN:
            default:
                return MetkaRoleBuilder.getUnknown().build();
            case READER:
                return MetkaRoleBuilder.getReader().build();
            case USER:
                return MetkaRoleBuilder.getUser().build();
            case TRANSLATOR:
                return MetkaRoleBuilder.getTranslator().build();
            case DATA_ADMIN:
                return MetkaRoleBuilder.getDataAdmin().build();
            case ADMIN:
                return MetkaRoleBuilder.getAdmin().build();
        }
    }

    @Override
    public int compareTo(MetkaRole o) {
        if(roleName.getValue() == o.roleName.getValue()) {
            return 0;
        } else if(roleName.getValue() < o.roleName.getValue()) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Returns the role that has more authority of the two given roles.
     * @param role1 String
     * @param role2 String
     * @return  String
     */
    public static String compareRoles(String role1, String role2) {
        MetkaRole r1 = MetkaRole.fromRoleName(MetkaRoleName.fromName(role1));
        MetkaRole r2 = MetkaRole.fromRoleName(MetkaRoleName.fromName(role2));
        int result = r1.compareTo(r2);
        if(result < 0) {
            return role2;
        } else if(result > 0) {
            return role1;
        } else {
            return role1;
        }
    }

    // Default states
    private final MetkaRoleName roleName;
    private final Language defaultLanguage;

    // Search permissions
    private final boolean canPerformSearch;
    private final boolean canSaveExpertSearch;
    private final boolean canRemoveNotOwnedExpertSearch;

    // General revision permissions
    private final boolean canViewRevision;
    private final boolean canCreateRevision;
    private final boolean canEditRevision;
    private final boolean canApproveRevision;
    private final boolean canForceClaimRevision;
    private final boolean canRemoveRevision;
    private final boolean canRestoreRevisions;

    // Import and Export permissions
    private final boolean canImportRevision;
    private final boolean canExportRevision;

    // Study specific permissions
    private final boolean canRemoveStudyVersions;
    private final boolean canAddStudyErrors;

    // Series specific permissions
    private final boolean canRemoveSeries;

    // Program settings permissions
    private final boolean canViewSettingsPage;
    private final boolean canGenerateReports;
    private final boolean canUploadConfigurations;
    private final boolean canUploadJson;

    private MetkaRole(MetkaRoleBuilder builder) {
        roleName                        = builder.roleName;
        defaultLanguage                 = builder.defaultLanguage;

        canPerformSearch                = builder.canPerformSearch;
        canSaveExpertSearch             = builder.canSaveExpertSearch;
        canRemoveNotOwnedExpertSearch   = builder.canRemoveNotOwnedExpertSearch;

        canViewRevision                 = builder.canViewRevision;
        canCreateRevision               = builder.canCreateRevision;
        canEditRevision                 = builder.canEditRevision;
        canApproveRevision              = builder.canApproveRevision;
        canForceClaimRevision           = builder.canForceClaimRevision;
        canRemoveRevision               = builder.canRemoveRevision;
        canRestoreRevisions             = builder.canRestoreRevisions;

        canImportRevision               = builder.canImportRevision;
        canExportRevision               = builder.canExportRevision;

        canRemoveStudyVersions          = builder.canRemoveStudyVersions;
        canAddStudyErrors               = builder.canAddStudyErrors;

        canRemoveSeries                 = builder.canRemoveSeries;

        canViewSettingsPage             = builder.canViewSettingsPage;
        canGenerateReports              = builder.canGenerateReports;
        canUploadConfigurations         = builder.canUploadConfigurations;
        canUploadJson                   = builder.canUploadJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetkaRole metkaRole = (MetkaRole) o;

        if (!roleName.equals(metkaRole.roleName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return roleName.hashCode();
    }

    @Override
    public String toString() {
        return roleName.name();
    }

    public String toJsonString() {
        String json = "{";

        json += "\"roleName\": \"" + roleName.name() + "\",";
        json += "\"defaultLanguage\": \""+defaultLanguage.toValue()+"\",";
        json += "\"canPerformSearch\": " + canPerformSearch + ",";
        json += "\"canSaveExpertSearch\": " + canSaveExpertSearch + ",";
        json += "\"canRemoveNotOwnedExpertSearch\": " + canRemoveNotOwnedExpertSearch + ",";
        json += "\"canViewRevision\": " + canViewRevision + ",";
        json += "\"canCreateRevision\": " + canCreateRevision + ",";
        json += "\"canEditRevision\": " + canEditRevision + ",";
        json += "\"canApproveRevision\": " + canApproveRevision + ",";
        json += "\"canForceClaimRevision\": " + canForceClaimRevision + ",";
        json += "\"canRemoveRevision\": " + canRemoveRevision + ",";
        json += "\"canRestoreRevisions\": " + canRestoreRevisions + ",";
        json += "\"canImportRevision\": " + canImportRevision + ",";
        json += "\"canExportRevision\": " + canExportRevision + ",";
        json += "\"canRemoveStudyVersions\": " + canRemoveStudyVersions + ",";
        json += "\"canAddStudyErrors\": " + canAddStudyErrors + ",";
        json += "\"canRemoveSeries\": " + canRemoveSeries + ",";
        json += "\"canViewSettingsPage\": " + canViewSettingsPage + ",";
        json += "\"canGenerateReports\": " + canGenerateReports + ",";
        json += "\"canUploadConfigurations\": " + canUploadConfigurations + ",";
        json += "\"canUploadJson\": " + canUploadJson;

        json += "}";
        return json;
    }

    public MetkaRoleName getRoleName() {
        return roleName;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public boolean isCanPerformSearch() {
        return canPerformSearch;
    }

    public boolean isCanSaveExpertSearch() {
        return canSaveExpertSearch;
    }

    public boolean isCanRemoveNotOwnedExpertSearch() {
        return canRemoveNotOwnedExpertSearch;
    }

    public boolean isCanViewRevision() {
        return canViewRevision;
    }

    public boolean isCanCreateRevision() {
        return canCreateRevision;
    }

    public boolean isCanEditRevision() {
        return canEditRevision;
    }

    public boolean isCanApproveRevision() {
        return canApproveRevision;
    }

    public boolean isCanForceClaimRevision() {
        return canForceClaimRevision;
    }

    public boolean isCanRemoveRevision() {
        return canRemoveRevision;
    }

    public boolean isCanRestoreRevisions() {
        return canRestoreRevisions;
    }

    public boolean isCanImportRevision() {
        return canImportRevision;
    }

    public boolean isCanExportRevision() {
        return canExportRevision;
    }

    public boolean isCanRemoveStudyVersions() {
        return canRemoveStudyVersions;
    }

    public boolean isCanAddStudyErrors() {
        return canAddStudyErrors;
    }

    public boolean isCanRemoveSeries() {
        return canRemoveSeries;
    }

    public boolean isCanViewSettingsPage() {
        return canViewSettingsPage;
    }

    public boolean isCanGenerateReports() {
        return canGenerateReports;
    }

    public boolean isCanUploadConfigurations() {
        return canUploadConfigurations;
    }

    public boolean isCanUploadJson() {
        return canUploadJson;
    }

    public static enum MetkaRoleName {
        UNKNOWN(0),
        READER(1),
        USER(2),
        TRANSLATOR(3),
        DATA_ADMIN(4),
        ADMIN(5);

        private final int value;

        private MetkaRoleName(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static MetkaRoleName fromName(String name) {
            switch(name) {
                case "UNKNONW":
                case "ROLE_UNKNOWN":
                default:
                    return UNKNOWN;
                case "READER":
                case "ROLE_READER":
                    return READER;
                case "USER":
                case "ROLE_USER":
                    return USER;
                case "TRANSLATOR":
                case "ROLE_TRANSLATOR":
                    return TRANSLATOR;
                case "DATA_ADMIN":
                case "ROLE_DATA_ADMIN":
                    return DATA_ADMIN;
                case "ADMIN":
                case "ROLE_ADMIN":
                    return ADMIN;
            }
        }

        public static MetkaRoleName fromValue(int value) {
            switch(value) {
                case 0:
                default:
                    return UNKNOWN;
                case 1:
                    return READER;
                case 2:
                    return USER;
                case 3:
                    return TRANSLATOR;
                case 4:
                    return DATA_ADMIN;
                case 5:
                    return ADMIN;
            }
        }
    }

    /**
     * Builds rule sets for MetkaRole.
     */
    private static class MetkaRoleBuilder {
        // Since permissions are layered the properties are listed above the static method that changes them for ease of reading.

        private MetkaRoleName roleName = MetkaRoleName.UNKNOWN;

        public static MetkaRoleBuilder getUnknown() {
            return new MetkaRoleBuilder();
        }

        private boolean canPerformSearch = false;
        private boolean canSaveExpertSearch = false;
        private boolean canViewRevision = false;

        private boolean canViewSettingsPage = false;
        private boolean canGenerateReports = false;

        public static MetkaRoleBuilder getReader() {
            MetkaRoleBuilder role = getUnknown();

            role.roleName = MetkaRoleName.READER;

            role.canPerformSearch = true;
            role.canSaveExpertSearch = true;
            role.canViewRevision = true;
            role.canViewSettingsPage = true;
            role.canGenerateReports = true;

            return role;
        }

        private boolean canRemoveNotOwnedExpertSearch = false;
        private boolean canCreateRevision = false;
        private boolean canEditRevision = false;
        private boolean canApproveRevision = false;
        private boolean canForceClaimRevision = false;
        private boolean canRemoveRevision = false;
        private boolean canImportRevision = false;
        private boolean canExportRevision = false;
        private boolean canAddStudyErrors = false;

        public static MetkaRoleBuilder getUser() {
            MetkaRoleBuilder role = getReader();

            role.roleName = MetkaRoleName.USER;

            role.canRemoveNotOwnedExpertSearch = true;
            role.canCreateRevision = true;
            role.canEditRevision = true;
            role.canApproveRevision = true;
            role.canForceClaimRevision = true;
            role.canRemoveRevision = true;
            role.canImportRevision = true;
            role.canExportRevision = true;
            role.canAddStudyErrors = true;


            return role;
        }

        private Language defaultLanguage = Language.DEFAULT;

        public static MetkaRoleBuilder getTranslator() {
            MetkaRoleBuilder role = getUser();

            role.roleName = MetkaRoleName.TRANSLATOR;
            role.defaultLanguage = Language.EN;

            return role;
        }

        private boolean canRestoreRevisions = false;
        private boolean canRemoveStudyVersions = false;
        private boolean canRemoveSeries = false;
        private boolean canUploadConfigurations = false;
        private boolean canUploadJson = false;

        public static MetkaRoleBuilder getDataAdmin() {
            MetkaRoleBuilder role = getUser();

            role.roleName = MetkaRoleName.DATA_ADMIN;

            role.canRestoreRevisions = true;
            role.canRemoveStudyVersions = true;
            role.canRemoveSeries = true;
            role.canUploadConfigurations = true;
            role.canUploadJson = true;

            return role;
        }

        public static MetkaRoleBuilder getAdmin() {
            MetkaRoleBuilder role = getUnknown();

            role.roleName = MetkaRoleName.ADMIN;

            return role;
        }

        public MetkaRole build() {
            return new MetkaRole(this);
        }
    }
}
