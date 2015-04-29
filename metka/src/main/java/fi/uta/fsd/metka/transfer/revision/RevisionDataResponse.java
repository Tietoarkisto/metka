package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.configuration.Configuration;
import fi.uta.fsd.metka.model.guiconfiguration.GUIConfiguration;
import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class RevisionDataResponse {
    private String result;
    private TransferData data;
    private Configuration configuration;
    private GUIConfiguration gui;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TransferData getData() {
        return data;
    }

    public void setData(TransferData data) {
        this.data = data;
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
