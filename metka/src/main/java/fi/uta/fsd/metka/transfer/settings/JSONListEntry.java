package fi.uta.fsd.metka.transfer.settings;

import fi.uta.fsd.metka.model.general.ConfigurationKey;

public class JSONListEntry {
    private UploadJsonRequest.JsonType type;
    private String title;
    private ConfigurationKey configKey;
    private String jsonKey;

    public UploadJsonRequest.JsonType getType() {
        return type;
    }

    public void setType(UploadJsonRequest.JsonType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ConfigurationKey getConfigKey() {
        return configKey;
    }

    public void setConfigKey(ConfigurationKey configKey) {
        this.configKey = configKey;
    }

    public String getJsonKey() {
        return jsonKey;
    }

    public void setJsonKey(String jsonKey) {
        this.jsonKey = jsonKey;
    }
}
