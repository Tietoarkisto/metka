package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.ConditionType;

import java.util.ArrayList;
import java.util.List;

public class Condition {
    private final Target target;
    private final ConditionType type;
    private final List<Condition> conditions = new ArrayList<>();

    @JsonCreator
    public Condition(@JsonProperty("target")Target target, @JsonProperty("type")ConditionType type) {
        this.target = target;
        this.type = type;
    }
}
