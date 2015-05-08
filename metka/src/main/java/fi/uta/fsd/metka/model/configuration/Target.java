package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.TargetType;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration_restrictions.graphml
 */
public class Target {
    private final TargetType type;
    private final String content;
    private final List<Target> targets = new ArrayList<>();
    private final List<Check> checks = new ArrayList<>();

    @JsonIgnore
    private Target parent;

    @JsonCreator
    public Target(@JsonProperty("type")TargetType type, @JsonProperty("content")String content) {
        this.type = type;
        this.content = content;
    }

    public TargetType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public Target getParent() {
        return parent;
    }

    public void setParent(Target parent) {
        this.parent = parent;
    }

    public void initParents(Target parent) {
        setParent(parent);
        for(Target t : targets) {
            t.parent = this;
            t.initParents(this);
        }
        for(Check c : checks) {
            c.setParent(this);
            c.initParents();
        }
    }

    public void initParents() {
        initParents(null);
    }

    public Target copy() {
        Target target = new Target(type, content);
        for(Target t : targets) {
            target.targets.add(t.copy());
        }
        for(Check c : checks) {
            target.checks.add(c.copy());
        }
        return target;
    }
}
