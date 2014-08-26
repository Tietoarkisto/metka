package fi.uta.fsd.metka.transfer.binder;

import fi.uta.fsd.metka.storage.repository.enums.ReturnResult;

import java.util.List;

public class BinderListResponse {
    private ReturnResult result;
    private List<BinderPageListEntry> pages;

    public ReturnResult getResult() {
        return result;
    }

    public void setResult(ReturnResult result) {
        this.result = result;
    }

    public List<BinderPageListEntry> getPages() {
        return pages;
    }

    public void setPages(List<BinderPageListEntry> pages) {
        this.pages = pages;
    }
}
