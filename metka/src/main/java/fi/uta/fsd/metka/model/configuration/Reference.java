package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.ReferenceType;

@JsonIgnoreProperties("_comment")
public class Reference {
    private final String key;
    private final ReferenceType type;
    private final String target;
    private final String valuePath;
    private String titlePath = null;
    private Boolean approvedOnly = false;
    private Boolean ignoreRemoved = false;

    @JsonCreator
    public Reference(@JsonProperty("key")String key, @JsonProperty("type")ReferenceType type, @JsonProperty("target")String target, @JsonProperty("valuePath")String valuePath) {
        this.key = key;
        this.type = type;
        this.target = target;
        this.valuePath = valuePath;
    }

    public String getKey() {
        return key;
    }

    public ReferenceType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }

    public String getValuePath() {
        return valuePath;
    }

    public String getTitlePath() {
        return titlePath;
    }

    public void setTitlePath(String titlePath) {
        this.titlePath = titlePath;
    }

    public Boolean getApprovedOnly() {
        return approvedOnly == null ? false : approvedOnly;
    }

    public void setApprovedOnly(Boolean approvedOnly) {
        this.approvedOnly = approvedOnly == null ? false : approvedOnly;
    }

    public Boolean getIgnoreRemoved() {
        return ignoreRemoved == null ? false : ignoreRemoved;
    }

    public void setIgnoreRemoved(Boolean ignoreRemoved) {
        this.ignoreRemoved = ignoreRemoved == null ? false : ignoreRemoved;
    }

    @JsonIgnore
    public String[] getValuePathParts() {
        if(valuePath == null) {
            return null;
        }
        String[] parts = valuePath.split("\\.");
        if(parts.length == 0) {
            parts = new String[1];
            parts[0] = valuePath;
        }
        return parts;
    }

    @JsonIgnore
    public String[] getTitlePathParts() {
        if(titlePath == null) {
            return null;
        }
        String[] parts = titlePath.split("\\.");
        if(parts.length > 0) {
            return parts;
        } else if(parts.length == 0 && titlePath != null && !titlePath.equals("")) {
            parts = new String[1];
            parts[0] = titlePath;
            return parts;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference = (Reference) o;

        if (!key.equals(reference.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
