package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.ConditionType;
/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_restrictions.graphml
 */
public class Condition {
    private final ConditionType type;
    private final Target target;

    @JsonIgnore
    private Check parent;

    @JsonCreator
    public Condition(@JsonProperty("type")ConditionType type, @JsonProperty("target")Target target) {
        this.type = type;
        this.target = target;
    }

    public Target getTarget() {
        return target;
    }

    public ConditionType getType() {
        return type;
    }

    public Check getParent() {
        return parent;
    }

    public void setParent(Check parent) {
        this.parent = parent;
    }

    public Condition copy() {
        Condition condition = new Condition(type, (target != null ? target.copy() : null));
        return condition;
    }
}
