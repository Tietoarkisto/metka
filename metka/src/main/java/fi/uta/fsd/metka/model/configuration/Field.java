package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.FieldType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

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
    @XmlElement private final String key;
    @XmlElement private Boolean translatable = true;
    @XmlElement private Boolean immutable = false;
    @XmlElement private Boolean display = true;
    @XmlElement private Integer maxValues = null;
    @XmlElement private String choicelist = null;
    @XmlElement private String section = null;
    @XmlElement private FieldType type;
    @XmlElement private Boolean subfield = false;
    @XmlElement private final List<String> subfields = new ArrayList<>();
    @XmlElement private Boolean unique = false;
    @XmlElement private Boolean required = false;
    @XmlElement private final List<String> concatenate = new ArrayList<>();
    @XmlElement private String reference = null;
    @XmlElement private Boolean multiline = false;
    @XmlElement private Boolean showSaveInfo = false;

    @JsonCreator
    public Field(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Boolean getTranslatable() {
        return translatable;
    }

    public void setTranslatable(Boolean translatable) {
        this.translatable = translatable;
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

    public String getChoicelist() {
        return choicelist;
    }

    public void setChoicelist(String choicelist) {
        this.choicelist = choicelist;
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

    public Boolean getSubfield() {
        return (subfield == null) ? false : subfield;
    }

    public void setSubfield(Boolean subfield) {
        this.subfield = (subfield == null) ? false : subfield;
    }

    public List<String> getSubfields() {
        return subfields;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public List<String> getConcatenate() {
        return concatenate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getMultiline() {
        return (multiline == null) ? false :multiline;
    }

    public void setMultiline(Boolean multiline) {
        this.multiline = (multiline == null) ? false : multiline;
    }

    public Boolean getShowSaveInfo() {
        return showSaveInfo;
    }

    public void setShowSaveInfo(Boolean showSaveInfo) {
        this.showSaveInfo = showSaveInfo;
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
