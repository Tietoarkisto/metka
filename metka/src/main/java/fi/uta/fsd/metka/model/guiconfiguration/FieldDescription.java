package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.data.enums.FieldType;
import fi.uta.fsd.metka.model.general.TranslationObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class FieldDescription {
    @XmlElement private final String key;
    @XmlElement private FieldType displayType; // Can be ignored for now, only defined here for completeness
    @XmlElement private Boolean multiline;
    @XmlElement private Boolean multichoice; // Can be ignored for now, only defined here for completeness
    @XmlElement private final List<String> columnFields = new ArrayList<>();
    @XmlElement private Boolean showSaveInfo;
    @XmlElement private Boolean showReferenceValue;
    @XmlElement private String handlerName;
    @XmlElement private TranslationObject dialogTitle; // TODO: This should be translation object

    @JsonCreator
    public FieldDescription(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public FieldType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(FieldType displayType) {
        this.displayType = displayType;
    }

    public Boolean getMultiline() {
        return (multiline == null) ? false : multiline;
    }

    public void setMultiline(Boolean multiline) {
        this.multiline = (multiline == null) ? false : multiline;
    }

    public Boolean getMultichoice() {
        return (multichoice == null) ? false : multichoice;
    }

    public void setMultichoice(Boolean multichoice) {
        this.multichoice = (multichoice == null) ? false : multichoice;
    }

    public List<String> getColumnFields() {
        return columnFields;
    }

    public Boolean getShowSaveInfo() {
        return (showSaveInfo == null) ? false : showSaveInfo;
    }

    public void setShowSaveInfo(Boolean showSaveInfo) {
        this.showSaveInfo = (showSaveInfo == null) ? false : showSaveInfo;
    }

    public Boolean getShowReferenceValue() {
        return (showReferenceValue == null) ? false : showReferenceValue;
    }

    public void setShowReferenceValue(Boolean showReferenceValue) {
        this.showReferenceValue = (showReferenceValue == null) ? false : showReferenceValue;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public TranslationObject getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(TranslationObject dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldDescription that = (FieldDescription) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}