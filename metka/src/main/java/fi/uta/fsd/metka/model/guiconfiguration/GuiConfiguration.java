package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.model.ModelBase;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class GuiConfiguration implements ModelBase {
    @XmlElement private ConfigurationKey key;
    @XmlElement private final List<GuiContainer> contents = new ArrayList<>();
}
