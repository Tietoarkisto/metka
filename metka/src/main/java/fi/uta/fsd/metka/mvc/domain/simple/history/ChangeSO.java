package fi.uta.fsd.metka.mvc.domain.simple.history;

import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.data.container.FieldContainer;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;
import fi.uta.fsd.metka.model.data.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple representation of a field change.
 */
public class ChangeSO {
    private String property;
    private String section;
    private final List<String> newValue = new ArrayList<String>();
    private final List<String> oldValue = new ArrayList<String>();
    private FieldType type;
    private ChangeOperation operation;
    private Integer maxValues;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<String> getNewValue() {
        return newValue;
    }

    public List<String> getOldValue() {
        return oldValue;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public ChangeOperation getOperation() {
        return operation;
    }

    public void setOperation(ChangeOperation operation) {
        this.operation = operation;
    }

    public Integer getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(Integer maxValues) {
        this.maxValues = maxValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeSO changeSO = (ChangeSO) o;

        if (!property.equals(changeSO.property)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    public static class ValueStringBuilder {
        public static void buildValueString(FieldType type, FieldContainer field, List<String> list) {
            // TODO: Add correct implementations for each FieldType, also the more complicated types might have requirements not yet present.
            switch(type) {
                case STRING:
                case INTEGER:
                case DATE:
                case DATETIME:
                case TIME:
                    buildSimpleValue((ValueFieldContainer)field, list);
                    break;
                default:
                    break;
            }
        }
    }

    private static void buildSimpleValue(ValueFieldContainer field, List<String> list) {
        for(Value value : field.getValues()) {
            list.add(value.toString());
        }
    }
}
