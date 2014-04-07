package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.mvc.domain.simple.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to request of ReferenceOptions
 */
public class ReferenceOptionsResponse {
    private final String key;
    private final Integer id;
    private final Integer revision;
    private List<ReferenceOption> options;
    private final List<ErrorMessage> messages = new ArrayList<>();

    public ReferenceOptionsResponse(String key, Integer id, Integer revision) {
        this.key = key;
        this.id = id;
        this.revision = revision;
    }

    public String getKey() {
        return key;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRevision() {
        return revision;
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

        if (!id.equals(that.id)) return false;
        if (!key.equals(that.key)) return false;
        if (!revision.equals(that.revision)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }
}
