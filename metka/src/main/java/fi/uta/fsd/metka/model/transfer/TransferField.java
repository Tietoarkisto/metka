package fi.uta.fsd.metka.model.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.uta.fsd.metka.enums.TransferFieldType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TransferField {
    @XmlElement private final String key;
    @XmlElement private final TransferFieldType type;
    @XmlElement private TransferValue value;
    @XmlElement private final List<TransferRow> rows = new ArrayList<>();

    @JsonCreator
    public TransferField(@JsonProperty("key")String key, @JsonProperty("type")TransferFieldType type) {
        this.key = key;
        this.type = type;
    }
}
