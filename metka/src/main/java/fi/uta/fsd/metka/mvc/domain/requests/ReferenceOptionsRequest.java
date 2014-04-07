package fi.uta.fsd.metka.mvc.domain.requests;

/**
 * Request object for collecting reference options for certain field
 */
public class ReferenceOptionsRequest {
    private String key;
    private Integer id;
    private Integer revision;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOptionsRequest that = (ReferenceOptionsRequest) o;

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
