package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.data.enums.ReferenceTitleType;

/**
 * Provides typed title for reference option
 */
public class ReferenceOptionTitle {
    private final ReferenceTitleType type;
    private final String value;

    public ReferenceOptionTitle(ReferenceTitleType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ReferenceTitleType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
