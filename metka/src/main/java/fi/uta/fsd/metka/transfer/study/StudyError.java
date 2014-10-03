package fi.uta.fsd.metka.transfer.study;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class StudyError {
    private Long id;
    private Long studyId;
    private String savedBy;
    private LocalDateTime savedAt;
    private Integer errorscore;
    private LocalDate errortriggerdate;
    private String errortriggerpro;
    private String errorlanguage;
    private String errorlabel;
    private String errornotes;
    private String errordatasetpart;
    private String errorpartsection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public Integer getErrorscore() {
        return errorscore;
    }

    public void setErrorscore(Integer errorscore) {
        this.errorscore = errorscore;
    }

    public LocalDate getErrortriggerdate() {
        return errortriggerdate;
    }

    public void setErrortriggerdate(LocalDate errortriggerdate) {
        this.errortriggerdate = errortriggerdate;
    }

    public String getErrortriggerpro() {
        return errortriggerpro;
    }

    public void setErrortriggerpro(String errortriggerpro) {
        this.errortriggerpro = errortriggerpro;
    }

    public String getErrorlanguage() {
        return errorlanguage;
    }

    public void setErrorlanguage(String errorlanguage) {
        this.errorlanguage = errorlanguage;
    }

    public String getErrorlabel() {
        return errorlabel;
    }

    public void setErrorlabel(String errorlabel) {
        this.errorlabel = errorlabel;
    }

    public String getErrornotes() {
        return errornotes;
    }

    public void setErrornotes(String errornotes) {
        this.errornotes = errornotes;
    }

    public String getErrordatasetpart() {
        return errordatasetpart;
    }

    public void setErrordatasetpart(String errordatasetpart) {
        this.errordatasetpart = errordatasetpart;
    }

    public String getErrorpartsection() {
        return errorpartsection;
    }

    public void setErrorpartsection(String errorpartsection) {
        this.errorpartsection = errorpartsection;
    }
}
