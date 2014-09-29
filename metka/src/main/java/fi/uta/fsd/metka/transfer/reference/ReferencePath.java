package fi.uta.fsd.metka.transfer.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.configuration.Reference;

public class ReferencePath {
    private final Reference reference;
    private final String value;
    private ReferencePath prev = null;
    private ReferencePath next = null;

    @JsonCreator
    public ReferencePath(@JsonProperty("reference") Reference reference, @JsonProperty("value") String value) {
        this.reference = reference;
        this.value = value;
    }

    public Reference getReference() {
        return reference;
    }

    public ReferencePath getPrev() {
        return prev;
    }

    public void setPrev(ReferencePath prev) {
        this.prev = prev;
    }

    public ReferencePath getNext() {
        return next;
    }

    public void setNext(ReferencePath next) {
        this.next = next;
    }

    public String getValue() {
        return value;
    }
}
