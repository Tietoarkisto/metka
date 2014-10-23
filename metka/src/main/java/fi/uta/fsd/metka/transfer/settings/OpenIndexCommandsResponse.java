package fi.uta.fsd.metka.transfer.settings;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

public class OpenIndexCommandsResponse {
    private Integer openCommands;
    private ReturnResult result;

    public Integer getOpenCommands() {
        return openCommands;
    }

    public void setOpenCommands(Integer openCommands) {
        this.openCommands = openCommands;
    }

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }
}
