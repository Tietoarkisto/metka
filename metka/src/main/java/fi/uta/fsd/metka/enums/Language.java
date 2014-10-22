package fi.uta.fsd.metka.enums;

public enum Language {
    DEFAULT("default"), // In this implementation default language is finnish
    EN("en"),
    SV("sv");

    private String value;
    private Language(String value) {
        this.value = value;
    }

    public String toValue() {
        return value;
    }
    public static Language fromValue(String value) {
        switch(value) {
            // Let's just take care of 'fi' in here
            case "default":
            case "fi":
                return DEFAULT;
            case "en":
                return EN;
            case "sv":
                return SV;
            default:
                throw new UnsupportedOperationException(value + " is not a valid language");
        }
    }

    private static final Language[] nonDefault = {
            EN,
            SV
    };

    public static Language[] nonDefaultLanguages() {
        return nonDefault;
    }
}
