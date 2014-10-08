package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldType;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties("_comment")
public class Field {
    private final String key;
    private FieldType type;
    private Boolean translatable = true;
    private Boolean immutable = false;
    private Boolean display = true;
    private Boolean unique = false;
    private Boolean required = false;
    private Integer maxValues = null;
    private String selectionList = null;
    private final List<String> concatenate = new ArrayList<>();
    private Boolean subfield = false;
    private final List<String> subfields = new ArrayList<>();
    private String reference = null;
    private Boolean editable = true;
    private Boolean writable = true;
    private Boolean indexed = true;
    private Boolean exact = true;
    private String bidirectional = "";
    private String indexName = null;

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

    public String getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(String selectionList) {
        this.selectionList = selectionList;
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
        return (exact == null) ? true : exact;
    }

    public void setExact(Boolean exact) {
        this.exact = (exact == null) ? true : exact;
    }

    public String getBidirectional() {
        return bidirectional;
    }

    public void setBidirectional(String bidirectional) {
        this.bidirectional = bidirectional;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    @JsonIgnore public String getIndexAs() {
        return StringUtils.hasText(indexName) ? indexName : key;
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
