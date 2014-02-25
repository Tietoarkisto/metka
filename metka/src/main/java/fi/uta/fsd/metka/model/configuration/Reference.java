package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Reference {
    @XmlElement private final String key;
    @XmlElement private final ConfigurationType targetType;
    @XmlElement private final String valueField;
    @XmlElement private String titleField;
    @XmlElement private Boolean approvedOnly = true;

    @JsonCreator
    public Reference(@JsonProperty("key")String key, @JsonProperty("targetType")ConfigurationType targetType, @JsonProperty("valueField")String valueField) {
        this.key = key;
        this.targetType = targetType;
        this.valueField = valueField;
    }

    public String getKey() {
        return key;
    }

    public ConfigurationType getTargetType() {
        return targetType;
    }

    public String getValueField() {
        return valueField;
    }

    public String getTitleField() {
        return titleField;
    }

    public void setTitleField(String titleField) {
        this.titleField = titleField;
    }

    public Boolean getApprovedOnly() {
        return (approvedOnly == null) ? true : approvedOnly;
    }

    public void setApprovedOnly(Boolean approvedOnly) {
        this.approvedOnly = (approvedOnly == null) ? true : approvedOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference reference = (Reference) o;

        if (targetType != reference.targetType) return false;
        if (!valueField.equals(reference.valueField)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = targetType.hashCode();
        result = 31 * result + valueField.hashCode();
        return result;
    }
}
