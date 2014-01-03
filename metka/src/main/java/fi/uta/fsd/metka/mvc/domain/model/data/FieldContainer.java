package fi.uta.fsd.metka.mvc.domain.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldContainer {
    @XmlElement private String fieldKey;
    @XmlElement private List<Value> values = new ArrayList<Value>();

    public FieldContainer() {
    }

    public FieldContainer(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public List<Value> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldContainer that = (FieldContainer) o;

        if (!fieldKey.equals(that.fieldKey)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fieldKey.hashCode();
    }
}
