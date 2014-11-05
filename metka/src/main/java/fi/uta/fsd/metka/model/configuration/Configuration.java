package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.SelectionListType;
import fi.uta.fsd.metka.model.general.ConfigurationKey;
import fi.uta.fsd.metka.model.interfaces.ModelBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specification and documentation is found from uml/data_config/uml_json_configuration.graphml
 */
@JsonIgnoreProperties({"_comment", "_help"})
public class Configuration implements ModelBase {
    private ConfigurationKey key;
    private final Map<String, Reference> references = new HashMap<>();
    private final Map<String, SelectionList> selectionLists = new HashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final List<Operation> restrictions = new ArrayList<>();
    private final Map<String, Target> namedTargets = new HashMap<>();
    private String displayId;
    private String hash; // no functionality for hash is implemented at this time.

    public Configuration() {
    }

    public Configuration(ConfigurationKey key) {
        this.key = key;
    }

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    public Map<String, Reference> getReferences() {
        return references;
    }

    public Map<String, SelectionList> getSelectionLists() {
        return selectionLists;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public List<Operation> getRestrictions() {
        return restrictions;
    }

    public Map<String, Target> getNamedTargets() {
        return namedTargets;
    }

    // Helper functions
    @JsonIgnore
    public Field getField(String key) {
        return fields.get(key);
    }

    @JsonIgnore
    public SelectionList getSelectionList(String key) {
        return selectionLists.get(key);
    }

    @JsonIgnore
    public SelectionList getRootSelectionList(String key) {
        SelectionList list = getSelectionList(key);
        List<SelectionList> foundLists = new ArrayList<>();
        foundLists.add(list);
        while(list.getType() == SelectionListType.SUBLIST) {
            key = list.getSublistKey();
            list = getSelectionList(key);
            if(!foundLists.contains(list)) {
                foundLists.add(list);
            } else {
                // There's a loop
                break;
            }
        }
        return list;
    }

    @JsonIgnore
    public Reference getReference(String key) {
        return references.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "Json[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }
}
