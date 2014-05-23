package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.model.general.TranslationObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class FieldTitle {
    @XmlElement private String key; // This should change to translatable object at some point.
    @XmlElement private TranslationObject title;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TranslationObject getTitle() {
        return title;
    }

    public void setTitle(TranslationObject title) {
        this.title = title;
    }
}
