package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.transfer.TransferData;

public class RevisionOperationResponse {
    private String result;
    private TransferData data;

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
}
