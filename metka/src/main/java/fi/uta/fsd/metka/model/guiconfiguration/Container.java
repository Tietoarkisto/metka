package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.data.enums.ContainerType;
import fi.uta.fsd.metka.data.enums.SectionState;
import fi.uta.fsd.metka.model.general.TranslationObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Container {
    private static int MAX_COLUMNS = 4;
    @XmlElement private ContainerType type;
    @XmlElement private TranslationObject title; // This should change to translatable object at some point.
    @XmlElement private Boolean hidden = false;
    @XmlElement private Boolean readOnly = null;
    @XmlElement private Boolean important = false;
    @XmlElement private SectionState defaultState = SectionState.CLOSE;
    @XmlElement private Integer columns = 1;
    @XmlElement private Boolean required = false;
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

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public Boolean getHidden() {
        return (hidden == null) ? false : hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = (hidden == null) ? false : hidden;
    }

    public Boolean getReadOnly() {
        return (readOnly != null && readOnly == false) ? null : readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = (readOnly != null && readOnly == false) ? null : readOnly;
    }

    public Boolean getImportant() {
        return (important == null) ? false : important;
    }

    public void setImportant(Boolean important) {
        this.important = (important == null) ? false : important;
    }

    public SectionState getDefaultState() {
        return (defaultState == null) ? SectionState.CLOSE : defaultState;
    }

    public void setDefaultState(SectionState defaultState) {
        this.defaultState = (defaultState == null) ? SectionState.CLOSE : defaultState;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns > MAX_COLUMNS
                ? MAX_COLUMNS
                : (columns < 1 ? 1 : columns);
    }

    public Boolean getRequired() {
        return (required == null) ? false : required;
    }

    public void setRequired(Boolean required) {
        this.required = (required == null) ? false : required;
    }

    public Integer getColspan() {
        return colspan;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan > MAX_COLUMNS
                ? MAX_COLUMNS
                : (colspan < 1 ? 1 : colspan);
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
