package fi.uta.fsd.metka.mvc.domain.model.data;

import fi.uta.fsd.metka.data.enums.ChangeOperation;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/18/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Change {
    @XmlElement private String fieldKey;
    @XmlElement private DateTime changeTime;
    @XmlElement private String user;
    @XmlElement private FieldContainer originalField;
    @XmlElement private FieldContainer newField;
    @XmlElement private ChangeOperation operation;
}
