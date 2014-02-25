package fi.uta.fsd.metka.mvc.domain.simple;

import fi.uta.fsd.metka.model.configuration.Configuration;

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
