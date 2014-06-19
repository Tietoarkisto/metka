package fi.uta.fsd.metkaSearch.commands.indexer;

public abstract class IndexerCommandBase implements IndexerCommand {
    private final Type type;
    private final Action action;

    protected IndexerCommandBase(Type type, Action action) {
        this.type = type;
        this.action = action;
    }

    public Type getType() {
        return type;
    }

    public Action getAction() {
        return action;
    }
}
