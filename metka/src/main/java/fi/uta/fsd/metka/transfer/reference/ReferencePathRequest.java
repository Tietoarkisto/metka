package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;

public class ReferencePathRequest {
    private String key;
    private String container;
    private Language language;
    private Boolean returnFirst = false;
    private ReferencePath root;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public ReferencePath getRoot() {
        return root;
    }

    public void setRoot(ReferencePath root) {
        this.root = root;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Boolean getReturnFirst() {
        return returnFirst == null ? false : returnFirst;
    }

    public void setReturnFirst(Boolean returnFirst) {
        this.returnFirst = (returnFirst == null ? false : returnFirst);
    }
}
