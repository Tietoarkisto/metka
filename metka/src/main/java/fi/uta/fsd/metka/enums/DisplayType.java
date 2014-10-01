package fi.uta.fsd.metka.enums;

/**
 * Custom display overrides for Field description.
 * Each of these encompasses some sort of miscellaneous and non general behavior
 */
public enum DisplayType {
    CUSTOM_JS,
    LINK;

    public static DisplayType fromValue(String s) {
        for(DisplayType d : values()) {
            if(d.name().equals(s)) {
                return d;
            }
        }
        throw new UnsupportedOperationException("Illegal type");
    }
}
