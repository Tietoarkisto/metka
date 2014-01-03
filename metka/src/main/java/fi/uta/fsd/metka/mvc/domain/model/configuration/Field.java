package fi.uta.fsd.metka.mvc.domain.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.data.enums.FieldType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 10:14 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Field {
    @XmlElement private String key;
    @XmlElement private Boolean translateable = true;
    @XmlElement private Boolean immutable = false;
    @XmlElement private Boolean display = true;
    @XmlElement private Integer maxValues;
    @XmlElement private String choiselist;
    @XmlElement private String section;
    @XmlElement private FieldType type;
    @XmlElement private String subfieldTo;

    public Field() {
    }

    public Field(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getTranslateable() {
        return translateable;
    }

    public void setTranslateable(Boolean translateable) {
        this.translateable = translateable;
    }

    public Boolean getImmutable() {
        return immutable;
    }

    public void setImmutable(Boolean immutable) {
        this.immutable = immutable;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Integer getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(Integer maxValues) {
        this.maxValues = maxValues;
    }

    public String getChoiselist() {
        return choiselist;
    }

    public void setChoiselist(String choiselist) {
        this.choiselist = choiselist;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public String getSubfieldTo() {
        return subfieldTo;
    }

    public void setSubfieldTo(String subfieldTo) {
        this.subfieldTo = subfieldTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (!key.equals(field.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
