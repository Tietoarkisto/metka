package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Request object for collecting reference options for certain field.
 */
public class ReferenceOptionsRequest {
    private String key;
    private String confType;
    private Integer confVersion;
    private Language language;
    private final Map<String, String> fieldValues = new HashMap<>();
    private String container;
    private Boolean returnFirst = false;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getConfType() {
        return confType;
    }

    public void setConfType(String confType) {
        this.confType = confType;
    }

    public Integer getConfVersion() {
        return confVersion;
    }

    public void setConfVersion(Integer confVersion) {
        this.confVersion = confVersion;
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Boolean getReturnFirst() {
        return returnFirst;
    }

    public void setReturnFirst(Boolean returnFirst) {
        this.returnFirst = returnFirst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOptionsRequest that = (ReferenceOptionsRequest) o;

        if (!confType.equals(that.confType)) return false;
        if (!confVersion.equals(that.confVersion)) return false;
        if (container != null ? !container.equals(that.container) : that.container != null) return false;
        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + confType.hashCode();
        result = 31 * result + confVersion.hashCode();
        result = 31 * result + (container != null ? container.hashCode() : 0);
        return result;
    }
}
