package fi.uta.fsd.metka.storage.cascade;

import fi.uta.fsd.metka.enums.OperationType;

/**
 * Contains a set of instructions for cascade operations
 */
public class CascadeInstruction {
    public static CascadeInstruction build(OperationType operation) throws UnsupportedOperationException {
        return CascadeInstruction.build(operation, null);
    }

    public static CascadeInstruction build(OperationType operation, Boolean draft) {
        return new CascadeInstruction(operation, draft);
    }

    private final OperationType operation;
    private final Boolean draft;

    private CascadeInstruction(OperationType operation, Boolean draft) {
        this.operation = operation;
        this.draft = draft;
    }

    public OperationType getOperation() {
        return operation;
    }

    public Boolean getDraft() {
        return draft;
    }
}
