package fi.uta.fsd.metkaExternal;

import fi.uta.fsd.metka.model.general.ConfigurationKey;

public class APIConfigurationReadRequest extends APIRequest {
    private ConfigurationKey key;

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }
}
