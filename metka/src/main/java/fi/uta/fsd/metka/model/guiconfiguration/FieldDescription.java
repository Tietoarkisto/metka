package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.FieldType;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties("_comment")
public class FieldDescription {
    private final String key;
    private FieldType displayType; // Can be ignored for now, only defined here for completeness
    private Boolean multiline;
    private Boolean multichoice; // Can be ignored for now, only defined here for completeness
    private final List<String> columnFields = new ArrayList<>();
    private Boolean showSaveInfo;
    private Boolean showReferenceValue;
    private String handlerName;
    private TranslationObject dialogTitle;

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