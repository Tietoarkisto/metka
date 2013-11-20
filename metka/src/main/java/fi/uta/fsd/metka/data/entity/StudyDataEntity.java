package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.StudyDataKey;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: MetkaDev1
 * Date: 11/13/13
 * Time: 10:39 AM
 */
@Entity
@Table(name="STUDY_DATA")
public class StudyDataEntity {
    @EmbeddedId
    private StudyDataKey key;

    @ManyToOne
    @JoinColumn(name = "STUDY_ID", insertable = false, updatable = false)
    private StudyEntity study;

    @Column(name = "TITLE")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private StudyState state;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.DATE)
    private Date lastModified;

    @ManyToOne
    @JoinColumn(name = "MODIFIED_BY")
    private PersonEntity modifiedBy;

    @ManyToOne
    @JoinColumn(name = "HANDLER")
    private PersonEntity handler;

    @Column(name = "DATA")
    @Lob
    private String data;

    // TODO: Link to different studies is still hazy

    @ManyToOne
    @JoinColumn(name = "TARGET_SERIES_ID")
    private SeriesEntity targetSeries;

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", key="+key+"]";
    }

    public static enum StudyState {
        DRAFT,
        APPROVED,
        PUBLISHED,
        IN_TRANSLATION
    }
}
