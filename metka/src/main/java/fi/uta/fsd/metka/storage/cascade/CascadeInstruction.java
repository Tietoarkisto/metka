package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.metka.enums.OperationType;
import fi.uta.fsd.metka.model.general.DateTimeUserPair;

/**
 * Contains a set of instructions for cascade operations
 */
public class CascadeInstruction {
    public static CascadeInstruction build(OperationType operation, DateTimeUserPair info) throws UnsupportedOperationException {
        return CascadeInstruction.build(operation, info, null);
    }

    public static CascadeInstruction build(OperationType operation, DateTimeUserPair info, Boolean draft) {
        return new CascadeInstruction(operation, info, draft);
    }

    private final OperationType operation;
    private final Boolean draft;
    private final DateTimeUserPair info;

    private CascadeInstruction(OperationType operation, DateTimeUserPair info, Boolean draft) {
        this.operation = operation;
        this.info = info;
        this.draft = draft;
    }

    public OperationType getOperation() {
        return operation;
    }

    public Boolean getDraft() {
        return draft;
    }

    public DateTimeUserPair getInfo() {
        return info;
    }
}
