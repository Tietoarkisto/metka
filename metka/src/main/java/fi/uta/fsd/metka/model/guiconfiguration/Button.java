package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.ButtonType;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties("_comment")
public class Button {
    private TranslationObject title;
    private final Set<String> permissions = new HashSet<>();
    private Boolean isHandler;
    private Boolean hasHandler;
    private final Set<VisibilityState> states = new HashSet<>();
    private ButtonType type;
    private String customHandler;

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Boolean getIsHandler() {
        return isHandler;
    }

    public void setIsHandler(Boolean isHandler) {
        this.isHandler = isHandler;
    }

    public Boolean getHasHandler() {
        return hasHandler;
    }

    public void setHasHandler(Boolean hasHandler) {
        this.hasHandler = hasHandler;
    }

    public Set<VisibilityState> getStates() {
        return states;
    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public String getCustomHandler() {
        return customHandler;
    }

    public void setCustomHandler(String customHandler) {
        this.customHandler = customHandler;
    }
}
