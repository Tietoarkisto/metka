package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.Language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response to request of ReferenceOptions
 */
public class ReferenceOptionsResponse {
    private final String key;
    private final String container;
    private final Language language;
    private final Map<String, String> fieldValues;

    private List<ReferenceOption> options;

    public ReferenceOptionsResponse(String key, String container, Language language, Map<String, String> fieldValues) {
        this.key = key;
        this.container = container;
        this.language = language;
        this.fieldValues = new HashMap<>();
        this.fieldValues.putAll(fieldValues);
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

    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    public List<ReferenceOption> getOptions() {
        return options;
    }

    public void setOptions(List<ReferenceOption> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOptionsResponse that = (ReferenceOptionsResponse) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        return result;
    }
}
