package fi.uta.fsd.metkaExternal.batch;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.model.general.RevisionKey;

public class IndexTarget {
    private RevisionKey key;
    private ConfigurationType type;

    public RevisionKey getKey() {
        return key;
    }

    public void setKey(RevisionKey key) {
        this.key = key;
    }

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
    }
}
