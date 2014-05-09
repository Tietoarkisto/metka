package fi.uta.fsd.metka.model.guiconfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.uta.fsd.metka.data.enums.ContainerType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties("_comment")
public class Container {
    @XmlElement private ContainerType type;
    @XmlElement private String title; // This should change to translatable object at some point.

}
