package fi.uta.fsd.metka.mvc.services.simple;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.mvc.services.simple.transfer.TransferObject;

/**
 * Return TransferObject and its related Configuration.
 */
public class RevisionViewDataContainer {
    private final TransferObject transferObject;
    private final Configuration configuration;

    public RevisionViewDataContainer(TransferObject transferObject, Configuration configuration) {
        this.transferObject = transferObject;
        this.configuration = configuration;
    }

    public TransferObject getTransferObject() {
        return transferObject;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
