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

    private Integer revision;
    private RevisionState state;
    private ConfigurationKey configuration;

    private Integer study_id;
    private String id;
    private Integer submissionid;
    private String title;
    private String datakind;
    private String ispublic;

    // External values
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

    public ConfigurationKey getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationKey configuration) {
        this.configuration = configuration;
    }

    // Field values
    public void setStudy_id(Integer study_id) {
        this.study_id = study_id;
    }

    public Integer getStudy_id() {
        return study_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSubmissionid() {
        return submissionid;
    }

    public void setSubmissionid(Integer submissionid) {
        this.submissionid = submissionid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatakind() {
        return datakind;
    }

    public void setDatakind(String datakind) {
        this.datakind = datakind;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudySingleSO that = (StudySingleSO) o;

        if (!revision.equals(that.revision)) return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Study object: [");
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
            case STUDY_ID:
                return study_id;
            case ID:
                return id;
            case SUBMISSIONID:
                return submissionid;
            case TITLE:
                return title;
            case DATAKIND:
                return datakind;
            case ISPUBLIC:
                return ispublic;
        }
        return null;
    }
}
