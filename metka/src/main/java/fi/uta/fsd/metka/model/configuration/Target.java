package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.TargetType;

public class Target {
    private final TargetType type;
    private final String target;

    @JsonCreator
    public Target(@JsonProperty("type")TargetType type, @JsonProperty("target")String target) {
        this.type = type;
        this.target = target;
    }

    public TargetType getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }
}
