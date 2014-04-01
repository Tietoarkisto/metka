package fi.uta.fsd.metka.model.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "translationData")
public class TranslationData extends RevisionData {
    @XmlElement private final Integer validFromRevision;
    //@XmlElement private Locale locale;

    public TranslationData(@JsonProperty("key") RevisionKey key,
                           @JsonProperty("configuration") ConfigurationKey configuration,
                           @JsonProperty("validFromRevision")Integer validFromRevision) {
        super(key, configuration, 1);
        this.validFromRevision = validFromRevision;
    }

    public Integer getValidFromRevision() {
        return validFromRevision;
    }
}
