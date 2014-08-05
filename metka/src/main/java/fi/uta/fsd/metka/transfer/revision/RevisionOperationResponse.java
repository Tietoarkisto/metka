package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.model.transfer.TransferData;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class RevisionOperationResponse {
    private ReturnResult result;
    private TransferData data;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public TransferData getData() {
        return data;
    }

    public void setData(TransferData data) {
        this.data = data;
    }
}
