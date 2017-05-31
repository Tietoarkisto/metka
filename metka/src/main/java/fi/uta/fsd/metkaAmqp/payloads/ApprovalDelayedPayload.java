package fi.uta.fsd.metkaAmqp.payloads;

import fi.uta.fsd.metka.enums.Language;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.model.data.container.ValueDataField;

/**
 * Created by juusoko on 31.5.2017.
 * juuso.korhonen@visma.com
 */
public class ApprovalDelayedPayload extends StudyPayload {
    private final String condition;
    private final String updated_at;

    public ApprovalDelayedPayload(RevisionData study, String message) {
        super(study);
        this.condition = message;
        this.updated_at = study.getSaved().getTime().toString();
    }

    public String getCondition() {
        return this.condition;
    }

    public String getUpdated_at() {
        return this.updated_at;
    }
}
