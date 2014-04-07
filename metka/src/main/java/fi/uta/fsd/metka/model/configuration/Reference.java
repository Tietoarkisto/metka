package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ReferenceType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Reference {
    @XmlElement private final String key;
    @XmlElement private final ReferenceType type;
    @XmlElement private final String target;
    @XmlElement private final String valuePath;
    @XmlElement private String titlePath = null;
    @XmlElement private Boolean approvedOnly = true;

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
        return approvedOnly;
    }

    public void setApprovedOnly(Boolean approvedOnly) {
        this.approvedOnly = approvedOnly;
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
