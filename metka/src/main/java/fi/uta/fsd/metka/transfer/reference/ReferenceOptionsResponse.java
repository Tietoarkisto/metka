package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to request of ReferenceOptions
 */
public class ReferenceOptionsResponse {
    private final String key;
    private final String container;
    private String dependencyValue;

    private List<ReferenceOption> options;
    private final List<ErrorMessage> messages = new ArrayList<>();

    public ReferenceOptionsResponse(String key, String container) {
        this.key = key;
        this.container = container;
    }

    public String getKey() {
        return key;
    }

    public String getContainer() {
        return container;
    }

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    public List<ReferenceOption> getOptions() {
        return options;
    }

    public void setOptions(List<ReferenceOption> options) {
        this.options = options;
    }

    public List<ErrorMessage> getMessages() {
        return messages;
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
