package fi.uta.fsd.metkaAuthentication;

import fi.uta.fsd.metka.enums.Language;

import java.util.HashSet;
import java.util.Set;

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

    private final Set<Permission> permissions = new HashSet<>();

    private MetkaRole(MetkaRoleBuilder builder) {
        roleName                        = builder.roleName;
        defaultLanguage                 = builder.defaultLanguage;

        // Add all builder defined permissions
        permissions.addAll(builder.permissions);
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
        json += "\"permissions\": {";
        boolean notFirst = false;
        for(Permission permission : Permission.values()) {
            if(notFirst) {
                json += ",";
            }
            json += "\""+permission.toPermission()+"\": "+permissions.contains(permission);
            if(!notFirst) {
                notFirst = true;
            }
        }
        json += "}";
        json += "}";
        return json;
    }

    public MetkaRoleName getRoleName() {
        return roleName;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermission(String permission) {
        // If given string is not a valid permission then throw exception.
        if(!Permission.isPermission(permission)) {
            throw new RuntimeException("Not a valid permission name");
        }
        return hasPermission(Permission.fromPermission(permission));
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
        private MetkaRoleName roleName = MetkaRoleName.UNKNOWN;
        private Language defaultLanguage = Language.DEFAULT;

        private Set<Permission> permissions = new HashSet<>();

        // Set all permissions to false so that we have a list of all permissions used by system.
        // This is mainly a concession to ease UI development so that we have every permission on json.
        public static MetkaRoleBuilder getUnknown() {
            MetkaRoleBuilder role = new MetkaRoleBuilder();

            return role;
        }

        public static MetkaRoleBuilder getReader() {
            return getUnknown()
                    .setRoleName(MetkaRoleName.READER)
                    .grant(Permission.HAS_MINIMUM_PERMISSION)
                    .grant(Permission.CAN_PERFORM_SEARCH)
                    .grant(Permission.CAN_SAVE_EXPERT_SEARCH)
                    .grant(Permission.CAN_VIEW_REVISION)
                    .grant(Permission.CAN_VIEW_SETTINGS_PAGE)
                    .grant(Permission.CAN_GENERATE_REPORTS)
                    .grant(Permission.CAN_VIEW_BINDER_PAGES)
                    .grant(Permission.CAN_VIEW_STUDY_ERRORS)
                    .grant(Permission.CAN_EDIT_STUDY_ERRORS);
        }

        public static MetkaRoleBuilder getUser() {
            return getReader()
                    .setRoleName(MetkaRoleName.USER)
                    .grant(Permission.CAN_REMOVE_NOT_OWNED_EXPERT_SEARCH)
                    .grant(Permission.CAN_CREATE_REVISION)
                    .grant(Permission.CAN_EDIT_REVISION)
                    .grant(Permission.CAN_APPROVE_REVISION)
                    .grant(Permission.CAN_FORCE_CLAIM_REVISION)
                    .grant(Permission.CAN_IMPORT_REVISION)
                    .grant(Permission.CAN_EXPORT_REVISION)
                    .grant(Permission.CAN_REMOVE_STUDY_ERRORS)
                    .grant(Permission.CAN_EDIT_BINDER_PAGES);
        }

        public static MetkaRoleBuilder getTranslator() {
            return getUser()
                    .setRoleName(MetkaRoleName.TRANSLATOR)
                    .setDefaultLanguage(Language.EN);
        }

        public static MetkaRoleBuilder getDataAdmin() {
            return getUser()
                    .setRoleName(MetkaRoleName.DATA_ADMIN)
                    .grant(Permission.CAN_FORCE_RELEASE_REVISION)
                    .grant(Permission.CAN_REMOVE_REVISION)
                    .grant(Permission.CAN_RESTORE_REVISION)
                    .grant(Permission.CAN_REMOVE_STUDY_VERSIONS)
                    .grant(Permission.CAN_UPLOAD_CONFIGURATIONS)
                    .grant(Permission.CAN_UPLOAD_JSON)
                    .grant(Permission.CAN_VIEW_INDEX_INFO);
        }

        public static MetkaRoleBuilder getAdmin() {
            return getDataAdmin()
                    .setRoleName(MetkaRoleName.ADMIN)
                    .grant(Permission.CAN_VIEW_API_USERS)
                    .grant(Permission.CAN_EDIT_API_USERS);
        }

        public MetkaRole build() {
            return new MetkaRole(this);
        }

        private MetkaRoleBuilder setRoleName(MetkaRoleName name) {
            this.roleName = name;
            return this;
        }

        private MetkaRoleBuilder setDefaultLanguage(Language language) {
            this.defaultLanguage = language;
            return this;
        }

        private MetkaRoleBuilder grant(Permission p) {
            permissions.add(p);
            return this;
        }
    }
}
