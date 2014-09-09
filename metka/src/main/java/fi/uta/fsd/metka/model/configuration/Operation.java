package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.OperationType;

import java.util.ArrayList;
import java.util.List;

public class Operation {
    private final OperationType type;
    private final List<Target> targets = new ArrayList<>();

    @JsonCreator
    public Operation(@JsonProperty("type")OperationType type) {
        this.type = type;
    }

    public OperationType getType() {
        return type;
    }

    public List<Target> getTargets() {
        return targets;
    }
}
