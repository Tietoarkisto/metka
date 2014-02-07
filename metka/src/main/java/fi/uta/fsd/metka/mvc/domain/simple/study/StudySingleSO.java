package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.data.enums.RevisionState;
import fi.uta.fsd.metka.model.configuration.ConfigurationKey;
import fi.uta.fsd.metka.mvc.domain.simple.SimpleObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/3/14
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudySingleSO extends SimpleObject {
    // TODO: Add study attributes
    private ConfigurationKey configuration;

    private Integer id;
    private Integer revision;
    private RevisionState state;

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationKey configuration) {
        this.configuration = configuration;
    }

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

    public RevisionState getState() {
        return state;
    }

    public void setState(RevisionState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study object: [");
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
            // TODO: Add attributes
        }
        return null;
    }

    private static enum VALUE {
        // TODO: Add attribtes
        ID("id");
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
