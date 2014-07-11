package fi.uta.fsd.metka.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.general.TranslationObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Option {
    @XmlElement private final String value;
    @XmlElement private Boolean deprecated = false;
    @XmlElement private TranslationObject title;

    @JsonCreator
    public Option(@JsonProperty("value")String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Helper method to return the default text from map.
     * If default text is null for some reason then returns empty string
     * @return
     */
    @JsonIgnore
    public String getDefaultTitle() {
        if(title == null) {
            return "";
        }
        String text = title.getTexts().get("default");
        return text == null ? "" : text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (!value.equals(option.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
