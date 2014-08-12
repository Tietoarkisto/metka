package fi.uta.fsd.metka.storage.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "STUDY_ERRORS")
public class StudyErrorEntity {
    @Id
    @SequenceGenerator(name="STUDY_ERRORS_ID_SEQ", sequenceName="STUDY_ERRORS_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STUDY_ERRORS_ID_SEQ")
    @Column(name = "STUDY_ERRORS_ID", updatable = false)
    private Long id;

    @Column(name = "STUDY_ID")
    private Long studyId;

    @Column(name = "STUDY_REVISION")
    private Integer studyRevision;

    @Column(name = "ADDED_BY")
    private String addedBy;

    @Column(name = "ADDED_AT")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime addedAt;

    @Column(name = "SCORE")
    private Integer score;

    @Column(name = "SECTION")
    private String section;

    @Column(name = "SUBSECTION")
    private String subsection;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRIGGER_DATE")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate triggerDate;

    @Column(name = "TRIGGER_TARGET")
    private String triggerTarget;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTriggerDate() {
        return triggerDate;
    }

    public void setTriggerDate(LocalDate triggerDate) {
        this.triggerDate = triggerDate;
    }

    public String getTriggerTarget() {
        return triggerTarget;
    }

    public void setTriggerTarget(String triggerTarget) {
        this.triggerTarget = triggerTarget;
    }
}
