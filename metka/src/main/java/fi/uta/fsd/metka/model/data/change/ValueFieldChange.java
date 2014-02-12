package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.model.data.container.ValueFieldContainer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ValueFieldChange extends FieldChange {
    @XmlElement private ValueFieldContainer newField;
    @XmlElement private ValueFieldContainer originalField;
    @XmlElement private ChangeOperation operation;

    @JsonCreator
    public ValueFieldChange(@JsonProperty("key") String key) {
        super(key);
    }

    public ValueFieldContainer getNewField() {
        return newField;
    }

    public void setNewField(ValueFieldContainer newField) {
        this.newField = newField;
    }

    public ValueFieldContainer getOriginalField() {
        return originalField;
    }

    public void setOriginalField(ValueFieldContainer originalField) {
        this.originalField = originalField;
    }

    public ChangeOperation getOperation() {
        return operation;
    }

    public void setOperation(ChangeOperation operation) {
        this.operation = operation;
    }
}
