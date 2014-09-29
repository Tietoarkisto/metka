package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;

import java.util.ArrayList;
import java.util.List;

public class ReferencePathResponse {
    private final String key;
    private final String container;
    private final Language language;
    private final ReferencePath root;

    private final List<ReferenceOption> options = new ArrayList<>();

    public ReferencePathResponse(String key, String container, Language language, ReferencePath root) {
        this.key = key;
        this.container = container;
        this.language = language;
        this.root = root;
    }

    public String getKey() {
        return key;
    }

    public String getContainer() {
        return container;
    }

    public Language getLanguage() {
        return language;
    }

    public ReferencePath getRoot() {
        return root;
    }

    public List<ReferenceOption> getOptions() {
        return options;
    }
}
