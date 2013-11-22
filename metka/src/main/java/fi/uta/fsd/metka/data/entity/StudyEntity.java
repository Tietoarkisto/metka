package fi.uta.fsd.metka.data.entity;

import fi.uta.fsd.metka.data.entity.key.RevisionDataKey;
import fi.uta.fsd.metka.data.enums.RevisionDataType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "STUDY")
public class StudyEntity {

    @Id
    @SequenceGenerator(name="STUDY_ID_SEQ", sequenceName="STUDY_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STUDY_ID_SEQ")
    @Column(name = "STUDY_ID", updatable = false)
    private Integer id;

    @Column(name = "REMOVED")
    private Boolean removed;

    /**
     * Shortcut to currently published revision.
     */
    @Column(name = "CURRENT_PUBLISHED_REVISION")
    private Integer currentPublishedRevision;

    @OneToMany
    @JoinColumn(name = "STUDY_ID", referencedColumnName = "TARGET_ID", insertable = false, updatable = false)
    private List<StudyRevisionEntity> revisionDataList;

    @ManyToMany
    @JoinTable(
            name = "BINDER_STUDY",
            joinColumns = {@JoinColumn(name = "STUDY_ID", referencedColumnName = "STUDY_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BINDER_ID", referencedColumnName = "BINDER_ID")}
    )
    private List<BinderEntity> binderList;

    @OneToMany(mappedBy = "targetStudy")
    private List<StudyErrorEntity> errorList;

    /*
    * All information that can change from revision to revision should be inside the data clob in revision data.
     */
    /*@OneToMany(mappedBy = "targetStudy")
    private List<StudyVersionEntity> studyVersionList;*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public List<BinderEntity> getBinderList() {
        return binderList;
    }

    public void setBinderList(List<BinderEntity> binderList) {
        this.binderList = binderList;
    }

    public List<StudyErrorEntity> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<StudyErrorEntity> errorList) {
        this.errorList = errorList;
    }

    @Override
    public String toString() {
        return "Entity[name="+this.getClass().getSimpleName()+", id="+id+"]";
    }
}
