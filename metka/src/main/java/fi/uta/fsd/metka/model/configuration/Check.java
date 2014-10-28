package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_restrictions.graphml
 */
public class Check {
    private final Condition condition;
    private final List<Target> restrictors = new ArrayList<>();

    @JsonIgnore
    private Target parent;

    @JsonCreator
    public Check(@JsonProperty("condition")Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public List<Target> getRestrictors() {
        return restrictors;
    }

    public Target getParent() {
        return parent;
    }

    public void setParent(Target parent) {
        this.parent = parent;
    }

    public void initParents() {
        condition.setParent(this);
        for(Target t : restrictors) {
            t.initParents();
        }
    }

    public Check copy() {
        Check check = new Check(condition.copy());
        for(Target t : restrictors) {
            check.restrictors.add(t.copy());
        }
        return check;
    }
}
