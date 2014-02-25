package fi.uta.fsd.metka.mvc.domain.simple.study;

import fi.uta.fsd.metka.mvc.domain.simple.SimpleSearchObject;

/**
 * Created with IntelliJ IDEA.
 * User: lasseku
 * Date: 2/3/14
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class StudySearchResultSO extends SimpleSearchObject {
    // TODO: Add study search result attributes
    private Integer id;
    private Integer revision;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study search result object: [");
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
            // TODO: Add search result attributes
        }
        return null;
    }
}
