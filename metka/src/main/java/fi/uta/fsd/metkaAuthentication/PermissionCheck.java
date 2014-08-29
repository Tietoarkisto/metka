package fi.uta.fsd.metkaAuthentication;

public enum PermissionCheck {
    PERMISSION,
    REMOVE_SEARCH;

    public static boolean isValid(String check) {
        for(PermissionCheck c : values()) {
            if(c.name().equals(check)) {
                return true;
            }
        }
        return false;
    }
}
