package fi.uta.fsd.metka.model.interfaces;

import fi.uta.fsd.metka.model.transfer.TransferField;

public interface TransferFieldContainer {
    public boolean hasField(String key);
    public TransferField getField(String key);
}
