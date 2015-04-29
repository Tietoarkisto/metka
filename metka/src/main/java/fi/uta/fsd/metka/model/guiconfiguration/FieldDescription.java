package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.DisplayType;
import fi.uta.fsd.metka.enums.Language;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification and documentation is found from uml/gui_config/uml_json_gui_configuration_FieldDescription.graphml
 */
@JsonIgnoreProperties("_comment")
public class FieldDescription {
    private final String key;
    private DisplayType displayType; // Can be ignored for now, only defined here for completeness
    private Boolean multiline;
    private Boolean multichoice; // Can be ignored for now, only defined here for completeness
    private final List<String> columnFields = new ArrayList<>();
    private Boolean showSaveInfo;
    private Boolean showReferenceValue;
    private Boolean displayHeader = true;
    private DialogTitle dialogTitle;
    private Boolean showReferenceSaveInfo = false;
    private final List<Language> showReferenceApproveInfo = new ArrayList<>();
    private Boolean showReferenceState = false;

    @JsonCreator
    public FieldDescription(@JsonProperty("key")String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
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

    public Boolean getDisplayHeader() {
        return displayHeader == null ? true : displayHeader;
    }

    public void setDisplayHeader(Boolean displayHeader) {
        this.displayHeader = displayHeader == null ? true : displayHeader;
    }

    public DialogTitle getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(DialogTitle dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public Boolean getShowReferenceSaveInfo() {
        return showReferenceSaveInfo == null ? false : showReferenceSaveInfo;
    }

    public void setShowReferenceSaveInfo(Boolean showReferenceSaveInfo) {
        this.showReferenceSaveInfo = (showReferenceSaveInfo == null ? false : showReferenceSaveInfo);
    }

    public List<Language> getShowReferenceApproveInfo() {
        return showReferenceApproveInfo;
    }

    public Boolean getShowReferenceState() {
        return showReferenceState == null ? false : showReferenceState;
    }

    public void setShowReferenceState(Boolean showReferenceState) {
        this.showReferenceState = (showReferenceState == null ? false : showReferenceState);
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