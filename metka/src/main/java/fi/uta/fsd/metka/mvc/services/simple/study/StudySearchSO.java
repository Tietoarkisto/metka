package fi.uta.fsd.metka.mvc.services.simple.study;

import fi.uta.fsd.metka.mvc.services.simple.SimpleSearchObject;

public class StudySearchSO extends SimpleSearchObject {
    // TODO: Add study search attributes

    private boolean all;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    /*private boolean searchApproved;
    private boolean searchDraft;
    private boolean searchRemoved;*/

    /*private final Map<String, Object> values = new HashMap<>();*/

    /*public boolean isSearchApproved() {
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

    public Map<String, Object> getValues() {
        return values;
    }*/

    /*@Override
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
        return values.get(key);
        *//*switch(StudyValues.fromString(key)) {
            // TODO: Add search attributes
            case TITLE:
                return title;
            case ID:
                return id;
            case SERIESID:
                return seriesid;
        }
        return null;*//*
    }*/
}
