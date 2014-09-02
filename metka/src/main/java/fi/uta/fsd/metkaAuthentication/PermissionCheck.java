package fi.uta.fsd.metkaAuthentication;

public enum PermissionCheck {
    PERMISSION(Values.PERMISSION),
    REMOVE_SEARCH(Values.REMOVE_SEARCH),
    RELEASE_REVISION(Values.RELEASE_REVISION),
    CLAIM_REVISION(Values.CLAIM_REVISION),
    IS_HANDLER(Values.IS_HANDLER);

    private final String check;

    private PermissionCheck(String check) {
        this.check = check;
    }

    public String getCheck() {
        return check;
    }

    public static boolean isValid(String check) {
        for(PermissionCheck c : values()) {
            if(c.check.equals(check)) {
                return true;
            }
        }
        return false;
    }

    public static PermissionCheck fromCheck(String check) {
        for(PermissionCheck c : values()) {
            if(c.check.equals(check)) {
                return c;
            }
        }
        throw new UnsupportedOperationException("Provided String is not a valid PermissionCheck value");
    }

    public static class Values {
        public static final String PERMISSION = "PERMISSION";
        public static final String REMOVE_SEARCH = "REMOVE_SEARCH";
        public static final String RELEASE_REVISION = "RELEASE_REVISION";
        public static final String CLAIM_REVISION = "CLAIM_REVISION";
        public static final String IS_HANDLER = "IS_HANDLER";
    }
}
