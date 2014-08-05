package fi.uta.fsd.metka.transfer.revision;

import java.util.HashMap;
import java.util.Map;

public class RevisionSearchRequest {
    private boolean searchApproved = true;
    private boolean searchDraft = true;
    private boolean searchRemoved;

    private final Map<String, String> values = new HashMap<>();

    public boolean isSearchApproved() {
        return searchApproved;
    }

    public void setSearchApproved(boolean searchApproved) {
        this.searchApproved = searchApproved;
    }

    public boolean isSearchDraft() {
        return searchDraft;
    }

    public void setSearchDraft(boolean searchDraft) {
        this.searchDraft = searchDraft;
    }

    public boolean isSearchRemoved() {
        return searchRemoved;
    }

    public void setSearchRemoved(boolean searchRemoved) {
        this.searchRemoved = searchRemoved;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getByKey(String key) {
        return values.containsKey(key) ? values.get(key) : null;
    }
}
