package fi.uta.fsd.metka.storage.collecting;

import fi.uta.fsd.metka.model.configuration.Reference;

class ReferencePath {
    private final Reference reference;
    private final String value;
    private ReferencePath prev = null;
    private ReferencePath next = null;

    ReferencePath(Reference reference, String value) {
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
