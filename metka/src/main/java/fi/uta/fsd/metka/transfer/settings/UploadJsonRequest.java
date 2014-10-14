package fi.uta.fsd.metka.transfer.settings;

public class UploadJsonRequest {
    private JsonType type;
    private String json;

    public JsonType getType() {
        return type;
    }

    public void setType(JsonType type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public static enum JsonType {
        DATA_CONF,
        GUI_CONF,
        MISC
    }
}
