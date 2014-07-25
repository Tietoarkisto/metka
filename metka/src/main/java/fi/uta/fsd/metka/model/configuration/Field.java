package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Field {
    @XmlElement private final String key;
    @XmlElement private Boolean translatable = true;
    @XmlElement private final List<String> notToLang = new ArrayList<>();
    @XmlElement private Boolean immutable = false;
    @XmlElement private Boolean display = true;
    @XmlElement private Integer maxValues = null;
    @XmlElement private String selectionList = null;
    @XmlElement private String section = null;
    @XmlElement private FieldType type;
    @XmlElement private Boolean subfield = false;
    @XmlElement private final List<String> subfields = new ArrayList<>();
    @XmlElement private Boolean unique = false;
    @XmlElement private Boolean required = false;
    @XmlElement private final List<String> concatenate = new ArrayList<>();
    @XmlElement private String reference = null;
    @XmlElement private String referenceKey = null;
    @XmlElement private Boolean showReferenceKey = false;
    @XmlElement private Boolean showSaveInfo = false;
    @XmlElement private Boolean multiline = false;
    @XmlElement private Boolean summaryField = true;
    @XmlElement private Boolean editable = true;
    @XmlElement private Boolean writable = true;
    @XmlElement private Boolean indexed = true;
    @XmlElement private Boolean exact = false;

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

    public List<String> getNotToLang() {
        return notToLang;
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

    public String getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(String selectionList) {
        this.selectionList = selectionList;
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

    public Boolean getSummaryField() {
        return (summaryField == null) ? true : summaryField;
    }

    public void setSummaryField(Boolean summaryField) {
        this.summaryField = (summaryField == null) ? true : summaryField;
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

    public String getReferenceKey() {
        return referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public Boolean getShowReferenceKey() {
        return showReferenceKey;
    }

    public void setShowReferenceKey(Boolean showReferenceKey) {
        this.showReferenceKey = showReferenceKey;
    }

    public Boolean getShowSaveInfo() {
        return showSaveInfo;
    }

    public void setShowSaveInfo(Boolean showSaveInfo) {
        this.showSaveInfo = showSaveInfo;
    }

    public Boolean getMultiline() {
        return (multiline == null) ? false :multiline;
    }

    public void setMultiline(Boolean multiline) {
        this.multiline = (multiline == null) ? false : multiline;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Boolean getIndexed() {
        return (indexed == null) ? true : indexed;
    }

    public void setIndexed(Boolean indexed) {
        this.indexed = (indexed == null) ? true : indexed;
    }

    public Boolean getExact() {
        return (exact == null) ? false : exact;
    }

    public void setExact(Boolean exact) {
        this.exact = (exact == null) ? false : exact;
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
