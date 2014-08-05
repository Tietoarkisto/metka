package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.enums.UIRevisionState;

import java.util.HashMap;
import java.util.Map;

public class RevisionSearchResult {
    private Long id;
    private Integer no;
    private UIRevisionState state;
    private final Map<String, String> values = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public UIRevisionState getState() {
        return state;
    }

    public void setState(UIRevisionState state) {
        this.state = state;
    }

    public Map<String, String> getValues() {
        return values;
    }
}
