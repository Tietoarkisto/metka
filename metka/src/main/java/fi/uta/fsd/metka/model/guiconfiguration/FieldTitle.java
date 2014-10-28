package fi.uta.fsd.metka.model.guiconfiguration;

import fi.uta.fsd.metka.model.general.TranslationObject;
/**
 * Specification and documentation is found from uml/gui_config/uml_json_gui_configuration.graphml
 */
public class FieldTitle {
    private String key;
    private TranslationObject title;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }
}
