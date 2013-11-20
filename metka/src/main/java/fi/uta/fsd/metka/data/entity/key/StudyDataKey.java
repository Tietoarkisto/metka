package fi.uta.fsd.metka.data.entity.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:39 AM
 */
@Embeddable
public class StudyDataKey implements Serializable {
    static final long serialVersionUID = 1L;

    @Column(name = "STUDY_ID", nullable = false, length = 30)
    private String studyId;

    @Column(name = "REVISION", nullable = false)
    private Integer revision;

    public StudyDataKey() {
    }

    public StudyDataKey(String studyId, Integer revision) {
        this.studyId = studyId;
        this.revision = revision;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    @Override
    public String toString() {
        return "Key[name="+this.getClass().getSimpleName()+", keys={studyId: "+studyId+", revision: "+revision+"}]";
    }
}
