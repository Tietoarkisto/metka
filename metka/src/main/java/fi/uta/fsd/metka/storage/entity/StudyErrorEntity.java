/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

package fi.uta.fsd.metka.storage.entity;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "STUDY_ERROR")
public class StudyErrorEntity {
    @Id
    @SequenceGenerator(name="STUDY_ERROR_ID_SEQ", sequenceName="STUDY_ERROR_ID_SEQ", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="STUDY_ERROR_ID_SEQ")
    @Column(name = "STUDY_ERROR_ID", updatable = false)
    private Long id;

    @Column(name = "STUDY_ERROR_STUDY", updatable = false)
    private Long studyErrorStudy;

    @Column(name = "SAVED_BY")
    private String savedBy;

    @Column(name = "SAVED_AT")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime savedAt;

    @Column(name = "ERROR_SCORE")
    private Integer errorscore;

    @Column(name = "ERROR_TRIGGER_DATE", nullable = true)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate errortriggerdate;

    @Column(name = "ERROR_TRIGGER_TARGET")
    private String errortriggerpro;

    @Column(name = "ERROR_LANGUAGE")
    private String errorlanguage;

    @Column(name = "ERROR_LABEL")
    private String errorlabel;

    @Column(name = "ERROR_NOTES")
    private String errornotes;

    @Column(name = "ERROR_DATASET_PART")
    private String errordatasetpart;

    @Column(name = "ERROR_PART_SECTION")
    private String errorpartsection;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudyErrorStudy() {
        return studyErrorStudy;
    }

    public void setStudyErrorStudy(Long studyErrorStudy) {
        this.studyErrorStudy = studyErrorStudy;
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
