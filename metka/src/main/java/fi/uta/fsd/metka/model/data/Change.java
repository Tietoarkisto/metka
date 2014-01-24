package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import fi.uta.fsd.metka.data.enums.ChangeOperation;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Change {
    @XmlElement private final String key;
    @XmlElement private FieldContainer originalField;
    @XmlElement private FieldContainer newField;
    @XmlElement private ChangeOperation operation;

    @JsonCreator
    public Change(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public FieldContainer getOriginalField() {
        return originalField;
    }

    public void setOriginalField(FieldContainer originalField) {
        this.originalField = originalField;
    }

    public FieldContainer getNewField() {
        return newField;
    }

    public void setNewField(FieldContainer newField) {
        this.newField = newField;
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

        Change change = (Change) o;

        if (!key.equals(change.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
