package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.DialogType;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties("_comment")
public class DialogTitle {
    private String key;
    private final Map<DialogType, TranslationObject> types = new HashMap<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<DialogType, TranslationObject> getTypes() {
        return types;
    }
}
