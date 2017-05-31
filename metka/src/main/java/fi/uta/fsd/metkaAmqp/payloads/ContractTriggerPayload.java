package fi.uta.fsd.metkaAmqp.payloads;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;

/**
 * Created by juusoko on 8.3.2017.
 * juuso.korhonen@visma.com
 */
public class ContractTriggerPayload extends StudyPayload {
    private final String triggerDate;
    private final String triggerPro;
    private final String label;

    public ContractTriggerPayload(RevisionData study) {
        super(study);
        this.triggerDate = ((ValueDataField)study.getField("triggerdate")).getActualValueFor(Language.DEFAULT);
        this.triggerPro = ((ValueDataField)study.getField("triggerpro")).getActualValueFor(Language.DEFAULT);
        this.label = ((ValueDataField)study.getField("triggerlabel")).getActualValueFor(Language.DEFAULT);
    }

    public String getTriggerDate() {
        return this.triggerDate;
    }

    public String getReceiver() {
        return this.triggerPro;
    }

    public String getLabel() {
        return this.label;
    }
}
