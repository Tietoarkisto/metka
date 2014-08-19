package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.enums.ButtonType;
import fi.uta.fsd.metka.enums.VisibilityState;
import fi.uta.fsd.metka.model.general.TranslationObject;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties("_comment")
public class Button {
    private TranslationObject title;
    private final List<String> userGroups = new ArrayList<>();
    private Boolean isHandler;
    private final List<VisibilityState> states = new ArrayList<>();
    private ButtonType type;

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public List<String> getUserGroups() {
        return userGroups;
    }

    public Boolean getHandler() {
        return isHandler;
    }

    public void setHandler(Boolean handler) {
        isHandler = handler;
    }

    public List<VisibilityState> getStates() {
        return states;
    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }
}
