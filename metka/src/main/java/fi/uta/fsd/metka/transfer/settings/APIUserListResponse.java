package fi.uta.fsd.metka.transfer.settings;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class APIUserListResponse {
    private ReturnResult result;
    private List<APIUserEntry> users = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<APIUserEntry> getUsers() {
        return users;
    }

    public void setUsers(List<APIUserEntry> users) {
        this.users = users;
    }
}
