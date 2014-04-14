package fi.uta.fsd.metka.transfer.reference;

/**
 * Request object for collecting reference options for certain field
 */
public class ReferenceOptionsRequest {
    private String key;
    private String confType;
    private Integer confVersion;
    private String dependencyValue;

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

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceOptionsRequest that = (ReferenceOptionsRequest) o;

        if (!confType.equals(that.confType)) return false;
        if (!confVersion.equals(that.confVersion)) return false;
        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + confType.hashCode();
        result = 31 * result + confVersion.hashCode();
        return result;
    }
}