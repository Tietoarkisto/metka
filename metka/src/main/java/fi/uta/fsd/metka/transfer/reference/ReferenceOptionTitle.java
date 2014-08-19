package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.enums.ReferenceTitleType;
import fi.uta.fsd.metka.model.general.TranslationObject;

/**
 * Provides typed title for reference option
 */
public class ReferenceOptionTitle {
    private final ReferenceTitleType type;
    private final TranslationObject value;

    public ReferenceOptionTitle(ReferenceTitleType type, TranslationObject value) {
        this.type = type;
        this.value = value;
    }

    public ReferenceTitleType getType() {
        return type;
    }

    public TranslationObject getValue() {
        return value;
    }
}
