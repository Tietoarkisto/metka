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
}
