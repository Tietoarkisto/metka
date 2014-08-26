package fi.uta.fsd.metka.transfer.revision;

import fi.uta.fsd.metka.enums.ConfigurationType;
import fi.uta.fsd.metka.enums.UIRevisionState;
import fi.uta.fsd.metka.model.data.RevisionData;
import fi.uta.fsd.metka.storage.response.RevisionableInfo;

import java.util.HashMap;
import java.util.Map;

public class RevisionSearchResult {
    public static RevisionSearchResult build(RevisionData data, RevisionableInfo info) {
        RevisionSearchResult searchResult = new RevisionSearchResult();
        searchResult.setId(data.getKey().getId());
        searchResult.setNo(data.getKey().getNo());
        searchResult.setState((info.getRemoved()) ? UIRevisionState.REMOVED : UIRevisionState.fromRevisionState(data.getState()));
        searchResult.setType(data.getConfiguration().getType());
        return searchResult;
    }

    private Long id;
    private Integer no;
    private UIRevisionState state;
    private ConfigurationType type;
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

    public ConfigurationType getType() {
        return type;
    }

    public void setType(ConfigurationType type) {
        this.type = type;
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
