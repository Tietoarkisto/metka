package fi.uta.fsd.metka.model.data.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import fi.uta.fsd.metka.model.data.RowIdentity;
import fi.uta.fsd.metka.model.data.container.RowContainer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RowChange {
    @XmlElement private final RowIdentity key;
    @XmlElement private RowContainer newField;
    @XmlElement private RowContainer originalField;
    @XmlElement private ChangeOperation operation;

    @JsonCreator
    public RowChange(@JsonProperty("key")RowIdentity key) {
        this.key = key;
    }

    public RowIdentity getKey() {
        return key;
    }

    public RowContainer getNewField() {
        return newField;
    }

    public void setNewField(RowContainer newField) {
        this.newField = newField;
    }

    public RowContainer getOriginalField() {
        return originalField;
    }

    public void setOriginalField(RowContainer originalField) {
        this.originalField = originalField;
    }

    public ChangeOperation getOperation() {
        return operation;
    }

    public void setOperation(ChangeOperation operation) {
        this.operation = operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RowChange rowChange = (RowChange) o;

        if (!key.equals(rowChange.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
