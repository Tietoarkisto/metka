package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ConfigurationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationKey {

    @XmlElement private final Integer version;
    @XmlElement private final ConfigurationType type;

    @JsonCreator
    public ConfigurationKey(@JsonProperty("type")ConfigurationType type, @JsonProperty("version")Integer version) {
        this.type = type;
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public ConfigurationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigurationKey that = (ConfigurationKey) o;

        if (type != that.type) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
