package fi.uta.fsd.metka.transfer.study;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class StudyError {
    private Long id;
    private String addedBy;
    private LocalDateTime addedAt;
    private Integer score;
    private String section;
    private String subsection;
    private String language;
    private String summary;
    private String description;
    private LocalDate triggerDate;
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
