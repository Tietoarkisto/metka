package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleSearchObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSearchSO extends SimpleSearchObject {
    private Integer id;
    private String name;
    private String abbreviation;
    private boolean searchApproved = true;
    private boolean searchDraft = true;
    private boolean searchRemoved;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series search object: [");
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
            case ID:
                return id;
            case ABBREVIATION:
                return abbreviation;
            case NAME:
                return name;
        }
        return null;
    }

    private static enum VALUE {
        ID("id"), ABBREVIATION("abbreviation"), NAME("name");
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
