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

package fi.uta.fsd.metka.storage.entity.impl;

import fi.uta.fsd.metka.storage.entity.RevisionableEntity;
import fi.uta.fsd.metka.enums.ConfigurationType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity class for study variable revisionable objects.
 * Unlike most of the revisionable entities this contains additional information namely the id of a
 * study variables revisionable object this variable belongs to and id of the variable saved using this object.
 * These are exclusively used to speed up data manipulation operations and should not be used outside of variable parsing.
 * None of these fields are key fields (primary or foreign) and are assumed to be created when the study variable is created.
 * All of these fields are immutable since they are integral part of single variables identity (even if they are not keys).
 * STUDY_ID what study this variable ultimately belongs to. Since this is immutable information and it's useful to know it makes sense
 * to have it on the study variable as well
 * STUDY_VARIABLES_ID what study variables collection this variable belongs to.
 * VARIABLE_ID this is the information gathered from the actual variable file. It's one of the first things determined and it's used to
 * identify variables when merging new variable files.
 */
@Entity
@DiscriminatorValue(ConfigurationType.Values.STUDY_VARIABLE)
public class StudyVariableEntity extends RevisionableEntity {
    @Column(name = "STUDY_VARIABLE_STUDY", updatable = false)
    private Long studyVariableStudy;

    @Column(name = "STUDY_VARIABLES_ID", updatable = false)
    private Long studyVariablesId;

    @Column(name = "VARID", updatable = false)
    private String varId;

    public Long getStudyVariableStudy() {
        return studyVariableStudy;
    }

    public void setStudyVariableStudy(Long studyVariableStudy) {
        this.studyVariableStudy = studyVariableStudy;
    }

    public Long getStudyVariablesId() {
        return studyVariablesId;
    }

    public void setStudyVariablesId(Long studyVariablesId) {
        this.studyVariablesId = studyVariablesId;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }
}
