package fi.uta.fsd.metka.transfer.binder;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.ArrayList;
import java.util.List;

public class BinderListResponse {
    private ReturnResult result;
    private final List<BinderPageListEntry> pages = new ArrayList<>();

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<BinderPageListEntry> getPages() {
        return pages;
    }
}
