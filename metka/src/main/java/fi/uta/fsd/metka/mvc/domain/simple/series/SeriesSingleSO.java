package fi.uta.fsd.metka.mvc.domain.simple.series;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 12/23/13
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SeriesSingleSO extends SimpleObject {
    private Integer id;
    private Integer revision;
    private String abbreviation;
    private String name;
    private String description;
    private RevisionState state;

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Series simple object: [");
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
            case DESCRIPTION:
                return description;
        }
        return null;
    }

    private static enum VALUE {
        ID("id"), ABBREVIATION("abbreviation"), NAME("name"), DESCRIPTION("description");
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
