package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.transfer.TransferData;

public class AdjacentRevisionRequest {
    private TransferData current;
    private Direction direction;
    private Boolean ignoreRemoved = true;

    public TransferData getCurrent() {
        return current;
    }

    public void setCurrent(TransferData current) {
        this.current = current;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Boolean getIgnoreRemoved() {
        return ignoreRemoved;
    }

    public void setIgnoreRemoved(Boolean ignoreRemoved) {
        this.ignoreRemoved = ignoreRemoved;
    }

    public static enum Direction {
        PREVIOUS, NEXT
    }
}
