package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;


public class StudySearchSO extends SimpleObject {
    // TODO: Add study search attributes

    private boolean searchApproved;
    private boolean searchDraft;
    private boolean searchRemoved;

    private String study_number;
    private String study_name;
    private Integer series_reference;

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

    public String getStudy_number() {
        return study_number;
    }

    public void setStudy_number(String study_number) {
        this.study_number = study_number;
    }

    public String getStudy_name() {
        return study_name;
    }

    public void setStudy_name(String study_name) {
        this.study_name = study_name;
    }

    public Integer getSeries_reference() {
        return series_reference;
    }

    public void setSeries_reference(Integer series_reference) {
        this.series_reference = series_reference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study search object: [");
        for(int i = 0; i < VALUE.values().length; i++) {
            VALUE v = VALUE.values()[i];
            sb.append(v.getKey()+": ");
            sb.append(getByKey(v.getKey()));
            if(i < VALUE.values().length-1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object getByKey(String key) throws IllegalArgumentException {
        switch(VALUE.fromString(key)) {
            // TODO: Add search attributes
            case STUDY_NAME:
                return study_name;
            case STUDY_NUMBER:
                return study_number;
            case SERIES_REFERENCE:
                return series_reference;
        }
        return null;
    }

    private static enum VALUE {
        // TODO: add search attribtes
        STUDY_NUMBER("study_number"), STUDY_NAME("study_name"), SERIES_REFERENCE("series_reference");
        private String key;

        private VALUE(String key) {
            this.key = key;
        }

        private String getKey() {
            return this.key;
        }

        private static VALUE fromString(String key) {
            if(key != null) {
                for(VALUE v : VALUE.values()) {
                    if(key.equals(v.key)) {
                        return v;
                    }
                }
            }
            throw new IllegalArgumentException("No value for ["+key+"] found.");
        }
    }
}
