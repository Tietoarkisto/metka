package fi.uta.fsd.metkaAmqp.payloads;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;

/**
 * Created by juusoko on 31.5.2017.
 * juuso.korhonen@visma.com
 */
public class ErrorTriggerPayload extends StudyPayload {
    private final String triggerdate;
    private final String receiver;
    private final String label;

    public ErrorTriggerPayload(RevisionData study) {
        super(study);
        this.triggerdate = ((ValueDataField)study.getField("errortriggerdate")).getActualValueFor(Language.DEFAULT);
        this.receiver = ((ValueDataField)study.getField("errortriggerpro")).getActualValueFor(Language.DEFAULT);
        this.label = ((ValueDataField)study.getField("errorlabel")).getActualValueFor(Language.DEFAULT);
    }

    public String getTriggerdate() {
        return this.triggerdate;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public String getLabel() {
        return this.label;
    }

}
