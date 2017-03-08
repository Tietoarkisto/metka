package fi.uta.fsd.metkaAmqp.payloads;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;

/**
 * Created by juusoko on 8.3.2017.
 * juuso.korhonen@visma.com
 */
public class ContractTriggerPayload extends StudyPayload {
    private final RevisionData trigger;

    public ContractTriggerPayload(RevisionData study) {
        super(study);
        this.trigger = study;
    }

    public String getTriggerDate() { return ((ValueDataField)trigger.getField("triggerdate")).getActualValueFor(Language.DEFAULT); }

    public String getReceiver() { return ((ValueDataField)trigger.getField("triggerpro")).getActualValueFor(Language.DEFAULT); }

    public String getLabel() { return ((ValueDataField)trigger.getField("triggerlabel")).getActualValueFor(Language.DEFAULT); }

}
