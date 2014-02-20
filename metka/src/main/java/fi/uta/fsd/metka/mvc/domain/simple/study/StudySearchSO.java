package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;


public class StudySearchSO extends SimpleObject {
    // TODO: Add study search attributes

    private boolean searchApproved;
    private boolean searchDraft;
    private boolean searchRemoved;

    private String id;
    private String title;
    private Integer seriesid;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSeriesid() {
        return seriesid;
    }

    public void setSeriesid(Integer seriesid) {
        this.seriesid = seriesid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study search object: [");
        for(int i = 0; i < StudyValues.values().length; i++) {
            StudyValues v = StudyValues.values()[i];
            sb.append(v.getKey()+": ");
            sb.append(getByKey(v.getKey()));
            if(i < StudyValues.values().length-1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object getByKey(String key) throws IllegalArgumentException {
        switch(StudyValues.fromString(key)) {
            // TODO: Add search attributes
            case TITLE:
                return title;
            case ID:
                return id;
            case SERIESID:
                return seriesid;
        }
        return null;
    }
}
