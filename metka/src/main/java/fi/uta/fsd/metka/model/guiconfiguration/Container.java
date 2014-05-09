package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.data.enums.ContainerType;
import fi.uta.fsd.metka.data.enums.SectionState;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Container {
    @XmlElement private ContainerType type;
    @XmlElement private String title; // This should change to translatable object at some point.
    @XmlElement private Boolean hidden = false;
    @XmlElement private Boolean readOnly = null;
    @XmlElement private Boolean important = false;
    @XmlElement private SectionState defaultState = SectionState.CLOSE;
    @XmlElement private Integer columns = 1;
    @XmlElement private Boolean required = null;
    @XmlElement private Integer colspan = 1;

    @XmlElement private FieldDescription field = null;

    @XmlElement private final List<Container> content = new ArrayList<>();
    @XmlElement private final List<Container> rows = new ArrayList<>();
    @XmlElement private final List<Container> cells = new ArrayList<>();

    public ContainerType getType() {
        return type;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Boolean getImportant() {
        return important;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public SectionState getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(SectionState defaultState) {
        this.defaultState = defaultState;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Integer getColspan() {
        return colspan;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan;
    }

    public FieldDescription getField() {
        return field;
    }

    public void setField(FieldDescription field) {
        this.field = field;
    }

    public List<Container> getContent() {
        return content;
    }

    public List<Container> getRows() {
        return rows;
    }

    public List<Container> getCells() {
        return cells;
    }
}
