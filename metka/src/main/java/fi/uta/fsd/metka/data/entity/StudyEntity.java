package fi.uta.fsd.metka.data.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "STUDY")
public class StudyEntity {

    @Id
    @Column(name="STUDY_ID", updatable = false, length = 30)
    private String id;

    @Column(name = "ARCHIVED")
    private Boolean archived;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL)
    private List<StudyDataEntity> studyDataEntityList;

    @ManyToMany
    @JoinTable(
            name = "BINDER_STUDY",
            joinColumns = {@JoinColumn(name = "STUDY_ID", referencedColumnName = "STUDY_ID")},
            inverseJoinColumns = {@JoinColumn(name = "BINDER_ID", referencedColumnName = "BINDER_ID")}
    )
    private List<BinderEntity> binderList;

    @OneToMany(mappedBy = "targetStudy")
    private List<StudyErrorEntity> errorList;

    @OneToMany(mappedBy = "targetStudy")
    private List<StudyVersionEntity> studyVersionList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public List<StudyDataEntity> getStudyDataEntityList() {
        return studyDataEntityList;
    }

    public void setStudyDataEntityList(List<StudyDataEntity> studyDataEntityList) {
        this.studyDataEntityList = studyDataEntityList;
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
