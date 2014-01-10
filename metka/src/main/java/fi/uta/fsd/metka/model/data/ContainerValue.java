package fi.uta.fsd.metka.model.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/19/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerValue extends Value {
    @XmlElement
    private Map<String, FieldContainer> fields = new HashMap<String, FieldContainer>();
}
