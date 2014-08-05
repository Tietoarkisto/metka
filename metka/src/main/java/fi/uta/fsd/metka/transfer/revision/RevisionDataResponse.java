package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class RevisionDataResponse {
    private ReturnResult result;
    private TransferData transferData;
    private Configuration configuration;
    private GUIConfiguration gui;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public TransferData getTransferData() {
        return transferData;
    }

    public void setTransferData(TransferData transferData) {
        this.transferData = transferData;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public GUIConfiguration getGui() {
        return gui;
    }

    public void setGui(GUIConfiguration gui) {
        this.gui = gui;
    }
}
