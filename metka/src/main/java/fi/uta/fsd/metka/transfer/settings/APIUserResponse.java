package fi.uta.fsd.metka.transfer.settings;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class APIUserResponse {
    private ReturnResult result;
    private final List<APIUserEntry> users = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<APIUserEntry> getUsers() {
        return users;
    }
}
