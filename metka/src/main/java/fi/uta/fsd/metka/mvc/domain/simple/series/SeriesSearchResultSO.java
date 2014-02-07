package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleSearchObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 1/30/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSearchResultSO extends SimpleSearchObject {
    private Integer id;
    private Integer revision;
    private String name;
    private String abbreviation;
    private String state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series search result object: [");
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
            case STATE:
                return state;
        }
        return null;
    }

    private static enum VALUE {
        ID("id"), ABBREVIATION("abbreviation"), NAME("name"), STATE("state");
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
