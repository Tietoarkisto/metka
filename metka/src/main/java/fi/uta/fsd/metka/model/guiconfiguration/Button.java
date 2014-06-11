package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.data.enums.ButtonType;
import fi.uta.fsd.metka.data.enums.VisibilityState;
import fi.uta.fsd.metka.model.general.TranslationObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Button {
    @XmlElement private TranslationObject title;
    @XmlElement private final List<String> userGroups = new ArrayList<>();
    @XmlElement private Boolean isHandler;
    @XmlElement private final List<VisibilityState> states = new ArrayList<>();
    @XmlElement private ButtonType type;

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
