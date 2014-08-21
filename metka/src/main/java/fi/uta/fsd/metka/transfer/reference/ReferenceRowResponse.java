package fi.uta.fsd.metka.transfer.reference;

import fi.uta.fsd.metka.model.transfer.TransferRow;
import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class ReferenceRowResponse {
    private ReturnResult result;
    private TransferRow row;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public TransferRow getRow() {
        return row;
    }

    public void setRow(TransferRow row) {
        this.row = row;
    }
}
