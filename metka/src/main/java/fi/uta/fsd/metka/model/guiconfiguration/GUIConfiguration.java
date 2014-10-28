package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.interfaces.ModelBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Specification and documentation is found from uml/gui_config/uml_json_gui_configuration.graphml
 */
@JsonIgnoreProperties("_comment")
public class GUIConfiguration implements ModelBase {
    private ConfigurationKey key;
    private final List<Container> content = new ArrayList<>();
    private final List<Button> buttons = new ArrayList<>();
    private final Map<String, FieldTitle> fieldTitles = new HashMap<>();

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    public List<Container> getContent() {
        return content;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public Map<String, FieldTitle> getFieldTitles() {
        return fieldTitles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GUIConfiguration that = (GUIConfiguration) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
