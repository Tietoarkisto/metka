package fi.uta.fsd.metka.transfer.study;

import fi.uta.fsd.metka.model.transfer.TransferData;

public class DDIImportRequest {
    private TransferData transferData;
    private String path;

    public TransferData getTransferData() {
        return transferData;
    }

    public void setTransferData(TransferData transferData) {
        this.transferData = transferData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
