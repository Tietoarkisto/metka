package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.enums.ConfigurationType;

import java.util.HashMap;
import java.util.Map;

public class RevisionCreateRequest {
    private ConfigurationType type;
    private final Map<String, String> parameters = new HashMap<>();

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
