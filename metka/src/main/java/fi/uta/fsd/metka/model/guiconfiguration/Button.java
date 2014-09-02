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
    private final Set<VisibilityState> states = new HashSet<>();
    private ButtonType type;

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Boolean getHandler() {
        return isHandler;
    }

    public void setHandler(Boolean handler) {
        isHandler = handler;
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
}
