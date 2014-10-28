package fi.uta.fsd.metka.model.general;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.ConfigurationType;
/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration.graphml
 */
public class ConfigurationKey {

    private final Integer version;
    private final ConfigurationType type;

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

    public ConfigurationKey copy() {
        return new ConfigurationKey(type, version);
    }

    @Override
    public String toString() {
        return "JsonKey[name="+this.getClass().getSimpleName()+", keys={type: "+type.toValue()+", version: "+version+"}]";
    }
}
