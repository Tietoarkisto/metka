package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/23/14
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Reference {
    @XmlElement private final ConfigurationType targetType;
    @XmlElement private final String valueField;
    @XmlElement private String titleField;

    public Reference(@JsonProperty("targetType")ConfigurationType targetType, @JsonProperty("valueField")String valueField) {
        this.targetType = targetType;
        this.valueField = valueField;
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
