package fi.uta.fsd.metka.transfer.reference;

/**
 * Single reference value/title pair.
 * This is always the result of one match of a reference path.
 * If multiple matches are found for a path then a List of ReferenceOptions should be used.
 */
public class ReferenceOption {
    private final String value;
    private final ReferenceOptionTitle title;

    public ReferenceOption(String value, ReferenceOptionTitle title) {
        this.value = value;
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public ReferenceOptionTitle getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOption that = (ReferenceOption) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
